/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURpositionE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.bot.managers.physics

import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.bot.entity.impl.WorldPlayerEntity
import dev.cubxity.mc.bot.pathing.AStar
import dev.cubxity.mc.protocol.entities.BlockPosition
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.client.player.ClientPlayerPositionAndLookPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.utils.BoundingBox
import dev.cubxity.mc.protocol.utils.ConversionUtil
import dev.cubxity.mc.protocol.utils.MathUtil
import dev.cubxity.mc.protocol.utils.Vec3d
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.*


class PhysicsManager(private val bot: Bot) {

    private var controlState = ControlState(
        forward = false,
        backward = false,
        left = false,
        right = false,
        jump = false,
        sprint = false
    )

    private var lastPhysicsFrameTime = System.currentTimeMillis()
    private var timeSinceOnGround = 0.0

    var position = SimplePosition(0.0, 0.0, 0.0)
    var vel = Vec3d(0.0, 0.0, 0.0)
    var onGround = true

    var pitch = 0.0f
    var yaw = 0.0f

    private var prevYaw = 0.0f
    private var targetYaw = 0.0f
    private var yawChanged = true

    private var lookJob: Job? = null
    private var walkJob: Job? = null

    init {
        GlobalScope.launch {
            while (true) {
                delay(50)

                val now = System.currentTimeMillis()
                val deltaSeconds = (now - lastPhysicsFrameTime) / 1000.0
                lastPhysicsFrameTime = now
                val deltaToUse = if (deltaSeconds < maxPhysicsDeltaSeconds) deltaSeconds else maxPhysicsDeltaSeconds
                tick(deltaToUse)
            }
        }

        bot.session.on<PacketReceivedEvent>()
            .filter { it.packet is ServerChatPacket }
            .map { it.packet as ServerChatPacket }
            .subscribe {
//                val pos = bot.world.findClosestEntity(50)?.pos ?: return@subscribe
                walkTo(SimplePosition(-40.0, 4.0, 75.0))
            }
    }

    private fun tick(deltaSeconds: Double) {
        if (!bot.world.isChunkLoaded(BlockPosition(position.x.toInt(), position.y.toInt(), position.z.toInt()))) return

        var movementRight = 0.0
        var movementForward = 0.0

        if (controlState.right) movementRight += 1.0
        if (controlState.left) movementRight -= 1.0
        if (controlState.forward) movementForward += 1.0
        if (controlState.backward) movementForward -= 1.0

        val acceleration = Vec3d(0.0, 0.0, 0.0)

        if (movementForward != 0.0 || movementRight != 0.0) {
            val rotationFromInput = atan2(-movementRight, movementForward)
            val inputYaw = yaw + rotationFromInput

            acceleration.x += walkingAcceleration * -sin(inputYaw)
            acceleration.z += walkingAcceleration * -cos(inputYaw)

            if (controlState.sprint) {
                acceleration.x *= sprintSpeed
                acceleration.z *= sprintSpeed
            }
        }

        if (controlState.jump && onGround && timeSinceOnGround > waitTimeBeforeNewJump) {
            vel.y = jumpSpeed
        }

        acceleration.y -= gravity

        val oldGroundSpeedSquared = calcGroundSpeedSquared()
        if (oldGroundSpeedSquared < epsilon) {
            vel.x = 0.0
            vel.z = 0.0
        } else {
            var oldGroundSpeed = sqrt(oldGroundSpeedSquared)
            oldGroundSpeed = if (oldGroundSpeed == 0.0) epsilon else oldGroundSpeed

            var groundFriction = groundFriction * walkingAcceleration
            if (!onGround) groundFriction *= 0.05

            val maybeNewGroundFriction = oldGroundSpeed / deltaSeconds
            groundFriction = if (groundFriction > maybeNewGroundFriction) maybeNewGroundFriction else groundFriction

            acceleration.x -= vel.x / oldGroundSpeed * groundFriction
            acceleration.z -= vel.z / oldGroundSpeed * groundFriction
        }

        vel.add(acceleration.scaled(deltaSeconds))

        var currentMaxGroundSpeed = maxGroundSpeed

        if (controlState.sprint) {
            currentMaxGroundSpeed *= sprintSpeed
        }

        val groundSpeedSquared = calcGroundSpeedSquared()
        if (groundSpeedSquared > currentMaxGroundSpeed * currentMaxGroundSpeed) {
            val groundSpeed = sqrt(groundSpeedSquared)
            val correctionScale = currentMaxGroundSpeed / groundSpeed
            vel.x *= correctionScale
            vel.z *= correctionScale
        }

        vel.y =
            if (vel.y < -terminalVelocity) -terminalVelocity else if (vel.y > terminalVelocity) terminalVelocity else vel.y

        var boundingBox = getBoundingBox()
        var boundingBoxMin: BlockPosition
        var boundingBoxMax: BlockPosition

        val bbmx = boundingBox.min.x.toInt()
        val bbmy = boundingBox.min.y.toInt()
        val bbmz = boundingBox.min.z.toInt()

        val bbmax = boundingBox.max.x.toInt()
        val bbmay = boundingBox.max.y.toInt()
        val bbmaz = boundingBox.max.z.toInt()

        if (vel.x != 0.0) {
            position.x += vel.x * deltaSeconds
            val blockX = floor(position.x + sign(vel.x) * playerApothem).toInt()

            boundingBoxMin = BlockPosition(blockX, bbmy, bbmz)
            boundingBoxMax = BlockPosition(blockX, bbmay, bbmaz)

            if (collisionInRange(boundingBoxMin, boundingBoxMax)) {
                position.x = blockX + (if (vel.x < 0) 1 + playerApothem else -playerApothem) * 1.001
                vel.x = 0.0
                boundingBox = getBoundingBox()
            }
        }

        if (vel.z != 0.0) {
            position.z += vel.z * deltaSeconds
            val blockZ = floor(position.z + sign(vel.z) * playerApothem).toInt()

            boundingBoxMin = BlockPosition(bbmx, bbmy, blockZ)
            boundingBoxMax = BlockPosition(bbmax, bbmay, blockZ)

            if (collisionInRange(boundingBoxMin, boundingBoxMax)) {
                position.z = blockZ + (if (vel.z < 0) 1 + playerApothem else -playerApothem) * 1.001
                vel.z = 0.0
                boundingBox = getBoundingBox()
            }
        }

        onGround = false
        if (vel.y != 0.0) {
            position.y += vel.y * deltaSeconds
            val playerHalfHeight = playerHeight / 2
            val blockY = floor(position.y + playerHalfHeight + sign(vel.y) * playerHalfHeight).toInt()

            boundingBoxMin = BlockPosition(bbmx, blockY, bbmz)
            boundingBoxMax = BlockPosition(bbmax, blockY, bbmaz)

            if (collisionInRange(boundingBoxMin, boundingBoxMax)) {
                position.y = blockY + (if (vel.y < 0) 1.0 else -playerHeight) * 1.001
                onGround = if (vel.y < 0) true else onGround
                vel.y = 0.0
            }
        }

        if (onGround) {
            timeSinceOnGround += deltaSeconds
        } else {
            timeSinceOnGround = 0.0
        }

        var deltaYaw = MathUtil.euclideanMod((targetYaw - prevYaw).toDouble(), PI * 2)
        deltaYaw = if (deltaYaw < 0)
            (if (deltaYaw < -PI) deltaYaw + (PI * 2) else deltaYaw)
        else (if (deltaYaw > PI) deltaYaw - (PI * 2) else deltaYaw)

        val absDeltaYaw = abs(deltaYaw)
        val maxDeltaYaw = deltaSeconds * yawSpeed
        deltaYaw = if (absDeltaYaw > maxDeltaYaw) maxDeltaYaw * sign(deltaYaw) else deltaYaw

        prevYaw = ((prevYaw + deltaYaw) % (PI * 2)).toFloat()
        yaw = prevYaw

        yawChanged = true

        bot.session.send(
            ClientPlayerPositionAndLookPacket(
                position.x,
                position.y,
                position.z,
                ConversionUtil.toNotchianYaw(yaw),
                ConversionUtil.toNotchianPitch(pitch),
                onGround
            )
        )
    }

    fun lookAt(position: SimplePosition, cb: () -> Unit = {}) {
        if (lookJob?.isActive == true)
            lookJob?.cancel()

        val dx = position.x - this.position.x
        val dy = position.y - this.position.y
        val dz = position.z - this.position.z

        val yaw = atan2(-dx, -dz).toFloat()
        val groundDistance = sqrt(dx * dx + dz * dz)
        val pitch = atan2(dy, groundDistance).toFloat()

        this.targetYaw = yaw
        this.pitch = pitch

        yawChanged = false

        lookJob = GlobalScope.launch {
            while (isActive) {
                delay(50)
                val prev = this@PhysicsManager.prevYaw
                if (abs((prev - this@PhysicsManager.targetYaw) % (PI * 2)) < 0.001 && yawChanged) {
                    cb()
                    lookJob = null
                    break
                }
            }
        }
    }

    fun walkTo(pos: SimplePosition) {
        if (walkJob?.isActive == true)
            walkJob?.cancel()

        val start = position.toBlockPosition()

        val aStar = AStar(bot, start, pos.toBlockPosition(), Int.MAX_VALUE)
        val items = ArrayDeque(aStar.iterate() ?: return)

        walkJob = GlobalScope.launch {
            var current = items.poll()

            while (isActive) {
                val tile = current.getRealPosition(start)

                controlState.forward = true

                if (position.toVec3().distanceTo(tile.toVec3()) <= 1.5) {
                    current = items.poll()

                    if (current == null) {
                        controlState.forward = false
                        break
                    }
                }

                lookAt(SimplePosition(tile.x + 0.5, tile.y + 0.5, tile.z + 0.5))

                delay(50)
            }
        }
    }

    private fun collisionInRange(first: BlockPosition, second: BlockPosition): Boolean {
        for (x in first.x..second.x) {
            for (y in first.y..second.y) {
                for (z in first.z..second.z) {
                    if ((bot.world.getBlockAt(BlockPosition(x, y, z))?.id ?: 0) != 0) return true
                }
            }
        }

        return false
    }

    private fun calcGroundSpeedSquared(): Double {
        return vel.x * vel.x + vel.z * vel.z
    }

    private fun getBoundingBox(): BoundingBox {
        return BoundingBox(
            Vec3d(
                position.x - playerApothem,
                position.y,
                position.z - playerApothem
            ).floor(),
            Vec3d(
                position.x + playerApothem,
                position.y + playerHeight,
                position.z + playerApothem
            ).floor()
        )
    }
}