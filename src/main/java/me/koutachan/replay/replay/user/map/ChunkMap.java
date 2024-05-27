package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkDataBulk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.PacketMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChunkMap extends PacketMap<ReplayPacket> {
    private final Map<ChunkPair, IChunkWrapper> chunks = new HashMap<>();

    @Override
    public void onPacket(ReplayPacket packet) {
        PacketWrapper<?> packetWrapper = packet.toPacket();
        PacketTypeCommon packetType = packetWrapper.getPacketTypeData().getPacketType();
        PacketSide side = packetType.getSide();
        if (side == PacketSide.SERVER) {
            switch ((PacketType.Play.Server) packetType) {
                case CHUNK_DATA: {
                    WrapperPlayServerChunkData chunk = (WrapperPlayServerChunkData) packetWrapper;
                    chunks.put(new ChunkPair(chunk.getColumn().getX(), chunk.getColumn().getZ()), new ChunkWrapper(chunk));
                    break;
                }
                case MAP_CHUNK_BULK: {
                    WrapperPlayServerChunkDataBulk chunk = (WrapperPlayServerChunkDataBulk) packetWrapper;
                    for (int i = 0; i < chunk.getChunks().length; i++) {
                        int x = chunk.getX()[i];
                        int z = chunk.getZ()[i];
                        BaseChunk[] baseChunk = chunk.getChunks()[i];
                        //TODO: Fixes //TODO: TO INT[]
                        byte[] biomeData = chunk.getBiomeData()[i];
                        Column column = new Column(x, z, true, baseChunk, new TileEntity[0]);
                        chunks.put(new ChunkPair(x, z), new ChunkWrapper(column));
                    }
                    break;
                }
                case CHUNK_BIOMES: {
                    /* TODO: WAIT WRAPPERS */
                    break;
                }
                case UNLOAD_CHUNK: {
                    WrapperPlayServerUnloadChunk chunk = (WrapperPlayServerUnloadChunk) packetWrapper;
                    chunks.remove(new ChunkPair(chunk.getChunkZ(), chunk.getChunkZ()));
                    break;
                }
            }
        }
    }

    public void clear() {
        chunks.clear();
    }

    public static class ChunkWrapper implements IChunkWrapper {
        private final ReplayPacket chunk;

        // Because of packet events is shitty.
        public ChunkWrapper(WrapperPlayServerChunkData chunk) {
            this.chunk = new ReplayPacketImpl(chunk);
        }

        public ChunkWrapper(Column column) {
            this.chunk = new ReplayPacketImpl(new WrapperPlayServerChunkData(column));
        }

        @Override
        public ReplayPacket toPacket() {
            return chunk;
        }
    }

    public interface IChunkWrapper {
        ReplayPacket toPacket();
    }

    public static class ChunkPair {
        private final int x, z;

        public ChunkPair(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkPair)) return false;
            ChunkPair chunkPair = (ChunkPair) o;
            return x == chunkPair.x && z == chunkPair.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }

    @Override
    public List<ReplayPacket> toPacket() {
        return chunks.values().stream()
                .map(IChunkWrapper::toPacket)
                .collect(Collectors.toList());
    }
}