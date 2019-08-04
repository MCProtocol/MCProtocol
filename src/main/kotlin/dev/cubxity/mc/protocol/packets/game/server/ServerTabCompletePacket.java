/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server;

import dev.cubxity.mc.protocol.ProtocolVersion;
import dev.cubxity.mc.protocol.data.obj.ChatComponent;
import dev.cubxity.mc.protocol.net.NetInput;
import dev.cubxity.mc.protocol.net.NetOutput;
import dev.cubxity.mc.protocol.packets.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ServerTabCompletePacket extends Packet {
    private int id;
    private int start;
    private int length;
    private List<Match> matches;

    @Override
    public void read(@NotNull NetInput buf, @NotNull ProtocolVersion target) {
        id = buf.readVarInt();
        start = buf.readVarInt();
        length = buf.readVarInt();

        for (int i = 0; i < buf.readVarInt(); i++) {
            matches.add(new Match(buf.readString(), buf.readBoolean() ? new ChatComponent(buf.readString()) : null));
        }
    }

    @Override
    public void write(@NotNull NetOutput out, @NotNull ProtocolVersion target) {
        out.writeVarInt(id);
        out.writeVarInt(start);
        out.writeVarInt(length);

        out.writeVarInt(matches.size());

        for (Match match : matches) {
            out.writeString(match.match);

            if (match.tooltip != null) {
                out.writeBoolean(true);
                out.writeString(match.tooltip.getJson());
            } else {
                out.writeBoolean(false);
            }
        }
    }

    public static class Match {
        private String match;
        private ChatComponent tooltip;

        public Match(String match, ChatComponent tooltip) {
            this.match = match;
            this.tooltip = tooltip;
        }

        public String getMatch() {
            return match;
        }

        public ChatComponent getTooltip() {
            return tooltip;
        }

        @Override
        public String toString() {
            return "Match{" +
                    "match='" + match + '\'' +
                    ", tooltip=" + tooltip +
                    '}';
        }
    }
}
