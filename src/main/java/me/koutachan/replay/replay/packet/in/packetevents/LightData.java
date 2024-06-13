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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class LightData implements Cloneable {
    private boolean trustEdges;
    private BitSet blockLightMask;
    private BitSet skyLightMask;
    private BitSet emptyBlockLightMask;
    private BitSet emptySkyLightMask;
    private Integer skyLightCount;
    private Integer blockLightCount;
    private List<byte[]> skyLightList = new ArrayList<>();
    private List<byte[]> blockLightList = new ArrayList<>();

    public void read(PacketWrapper<?> packet) {
        ServerVersion serverVersion = packet.getServerVersion();
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            trustEdges = packet.readBoolean();
        }
        skyLightMask = ChunkBitMask.readChunkMask(packet);
        blockLightMask = ChunkBitMask.readChunkMask(packet);
        emptySkyLightMask = ChunkBitMask.readChunkMask(packet);
        emptyBlockLightMask = ChunkBitMask.readChunkMask(packet);

        skyLightCount = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17) ? packet.readVarInt() : 18;
        skyLightList = new ArrayList<>(skyLightCount);
        for (int x = 0; x < skyLightCount; x++) {
            if (skyLightMask.get(x)) {
                skyLightList.add(packet.readByteArray(2048));
            }
        }

        blockLightCount = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17) ? packet.readVarInt() : 18;
        blockLightList = new ArrayList<>(blockLightCount);
        for (int x = 0; x < blockLightCount; x++) {
            if (blockLightMask.get(x)) {
                blockLightList.add(packet.readByteArray(2048));
            }
        }
    }

    public void write(PacketWrapper<?> packet) {
        ServerVersion serverVersion = packet.getServerVersion();
        if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            packet.writeBoolean(trustEdges);
        }
        ChunkBitMask.writeChunkMask(packet, skyLightMask);
        ChunkBitMask.writeChunkMask(packet, blockLightMask);
        ChunkBitMask.writeChunkMask(packet, emptySkyLightMask);
        ChunkBitMask.writeChunkMask(packet, emptyBlockLightMask);
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            if (skyLightCount == null) {
                skyLightCount = skyLightList.size();
            }
            packet.writeVarInt(skyLightCount);
        }
        for (byte[] skyLightArray : skyLightList) {
            packet.writeByteArray(skyLightArray);
        }
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            if (blockLightCount == null) {
                blockLightCount = blockLightList.size();
            }
            packet.writeVarInt(blockLightCount);
        }
        for (byte[] blockLightArray : blockLightList) {
            packet.writeByteArray(blockLightArray);
        }
    }

    public boolean isTrustEdges() {
        return trustEdges;
    }

    public void setTrustEdges(boolean trustEdges) {
        this.trustEdges = trustEdges;
    }

    public BitSet getBlockLightMask() {
        return blockLightMask;
    }

    public void setBlockLightMask(BitSet blockLightMask) {
        this.blockLightMask = blockLightMask;
    }

    public BitSet getSkyLightMask() {
        return skyLightMask;
    }

    public void setSkyLightMask(BitSet skyLightMask) {
        this.skyLightMask = skyLightMask;
    }

    public BitSet getEmptyBlockLightMask() {
        return emptyBlockLightMask;
    }

    public void setEmptyBlockLightMask(BitSet emptyBlockLightMask) {
        this.emptyBlockLightMask = emptyBlockLightMask;
    }

    public BitSet getEmptySkyLightMask() {
        return emptySkyLightMask;
    }

    public void setEmptySkyLightMask(BitSet emptySkyLightMask) {
        this.emptySkyLightMask = emptySkyLightMask;
    }

    public int getSkyLightCount() {
        return skyLightCount;
    }

    public void setSkyLightCount(int skyLightCount) {
        this.skyLightCount = skyLightCount;
    }

    public int getBlockLightCount() {
        return blockLightCount;
    }

    public void setBlockLightCount(int blockLightCount) {
        this.blockLightCount = blockLightCount;
    }

    public List<byte[]> getSkyLightList() {
        return skyLightList;
    }

    public void setSkyLightList(List<byte[]> skyLightList) {
        this.skyLightList = skyLightList;
    }

    public List<byte[]> getBlockLightList() {
        return blockLightList;
    }

    public void setBlockLightList(List<byte[]> blockLightList) {
        this.blockLightList = blockLightList;
    }

    @Override
    public LightData clone() {
        LightData clone = new LightData();
        clone.trustEdges = trustEdges;
        clone.blockLightMask = (BitSet) blockLightMask.clone();
        clone.skyLightMask = (BitSet) skyLightMask.clone();
        clone.emptyBlockLightMask = (BitSet) emptyBlockLightMask.clone();
        clone.emptySkyLightMask = (BitSet) emptySkyLightMask.clone();
        clone.skyLightList = new ArrayList<>(skyLightList);
        clone.blockLightList = new ArrayList<>(blockLightList);
        return clone;
    }
}
