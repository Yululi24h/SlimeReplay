/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2022 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.koutachan.replay.replay.packet.in.packetevents;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.BitSet;

public class ChunkBitMask {
    public static long[] readBitSetLongs(PacketWrapper<?> packet) {
        if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
            //Read primary bit mask
            return packet.readLongArray();
        } else if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
            //Read primary bit mask
            return new long[]{packet.readVarInt()};
        } else {
            //Read primary bit mask
            return new long[]{packet.readUnsignedShort()};
        }
    }

    public static BitSet readChunkMask(PacketWrapper<?> packet) {
        return BitSet.valueOf(readBitSetLongs(packet));
    }

    public static void writeChunkMask(PacketWrapper<?> packet, BitSet chunkMask) {
        if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
            //Write primary bit mask
            long[] longArray = chunkMask.toLongArray();
            packet.writeLongArray(longArray);
        } else if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
            //Write primary bit mask
            packet.writeVarInt((int) chunkMask.toLongArray()[0]);
        } else {
            packet.writeShort((int) chunkMask.toLongArray()[0]);
        }
    }
}