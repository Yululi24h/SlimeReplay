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

package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerChunkData;
import me.koutachan.replay.replay.user.map.ChunkCache;

import java.util.ArrayList;
import java.util.List;

public class ReplayChunkData extends ReplayWrapper<ReplayChunkData> {
    private Column column;
    private LightData lightData;
    private boolean ignoreOldData;
    private int chunkSize;

    public ReplayChunkData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayChunkData(PacketSendEvent event) {
        WrapperPlayServerChunkData chunkData = new WrapperPlayServerChunkData(event);
        this.column = chunkData.getColumn();
        this.lightData = chunkData.getLightData();
        this.ignoreOldData = chunkData.isIgnoreOldData();
    }

    public ReplayChunkData(Column column, LightData lightData, boolean ignoreOldData) {
        this.column = column;
        this.lightData = lightData;
        this.ignoreOldData = ignoreOldData;
    }

    @Override
    public void read() {
        this.chunkSize = 16;
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            this.chunkSize = readVarInt();
        }
        WrapperPlayServerChunkData chunkData = readWrapper(new WrapperPlayServerChunkData(this.chunkSize));
        this.column = chunkData.getColumn();
        this.lightData = chunkData.getLightData();
        this.ignoreOldData = chunkData.isIgnoreOldData();
    }

    @Override
    public void write() {
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
            writeVarInt(this.column.getChunks().length);
        }
        writeWrapper(createChunkData());
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    public WrapperPlayServerChunkData createChunkData() {
        return new WrapperPlayServerChunkData(this.column, this.lightData, this.ignoreOldData, this.chunkSize);
    }

    public Column getColumn() {
        return column;
    }

    public int getX() {
        return column.getX();
    }

    public int getZ() {
        return column.getZ();
    }

    public ChunkCache.ChunkPos toChunkPos() {
        return new ChunkCache.ChunkPos(getX(), getZ());
    }

    public void setLightData(LightData lightData) {
        this.lightData = lightData;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(createChunkData());
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerUnloadChunk(this.column.getX(), this.column.getZ()));
        return packets;
    }
}