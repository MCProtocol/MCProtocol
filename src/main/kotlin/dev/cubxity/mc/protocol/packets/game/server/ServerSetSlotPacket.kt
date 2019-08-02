package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerSetSlotPacket(
    var windowId: Int,
    var slot: Int,
    var data: Slot
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        windowId = buf.readByte().toInt()
        slot = buf.readShort().toInt()
        data = buf.readSlot()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeByte(windowId)
        out.writeShort(slot.toShort())
        out.writeSlot(data)
    }
}