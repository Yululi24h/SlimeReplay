package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.PacketMap;

public class ChunkMap extends PacketMap<ReplayPacket> {
    @Override
    public void onPacket(ReplayPacket packet) {
        PacketWrapper<?> packetWrapper = packet.toPacket();
        if (packetWrapper instanceof WrapperPlayServerChunkData) {
            WrapperPlayServerChunkData chunk = (WrapperPlayServerChunkData) packetWrapper;
            register(toChunkPair(chunk.getColumn()), packet);
        } else if (packetWrapper instanceof WrapperPlayServerChunkDataBulk) {
            WrapperPlayServerChunkDataBulk chunk = (WrapperPlayServerChunkDataBulk) packetWrapper;
            for (int i = 0; i < chunk.getChunks().length; i++) {
                int x = chunk.getX()[i];
                int z = chunk.getZ()[i];
                BaseChunk baseChunk = chunk.getChunks()[x][z];
                //TODO: Fixes
                byte biomeData = chunk.getBiomeData()[x][z];
                Column column = new Column(x, z, false, new BaseChunk[] {baseChunk}, new TileEntity[0]);
                register(toChunkPair(column), new ReplayPacketImpl(new WrapperPlayServerChunkData(column)));
            }
        } else if (packetWrapper instanceof WrapperPlayServerUnloadChunk) {
            WrapperPlayServerUnloadChunk chunk = (WrapperPlayServerUnloadChunk) packetWrapper;
            unregister(new ChunkPair(chunk.getChunkX(), chunk.getChunkZ()));
        }
    }

    public ChunkPair toChunkPair(Column column) {
        return new ChunkPair(column.getX(), column.getZ());
    }

}