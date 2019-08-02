package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerWindowItemsPacket(
    var windowId: Int,
    var slotData: Array<Slot>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        windowId = buf.readUnsignedByte()

        slotData = arrayOf()
        for (i in 0 until buf.readShort()) {
            slotData += buf.readSlot()
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeByte(windowId)
        out.writeShort(slotData.size.toShort())

        for (data in slotData) {
            out.writeSlot(data)
        }
    }
}