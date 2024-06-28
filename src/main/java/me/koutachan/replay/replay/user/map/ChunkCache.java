package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.util.Vector3i;
import com.google.common.cache.CacheBuilder;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.utils.LightDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChunkCache {
    private final Map<ChunkPos, IChunkWrapper> chunks = new HashMap<>();
    private final ReplayUser user;
    private final Map<ChunkPos, LightData> lightQueue = CacheBuilder.newBuilder() // This is not the best way to implement this, but it is better than creating Minecraft's light engine.
            .maximumSize(10)
            .<ChunkPos, LightData>build()
            .asMap();

    public ChunkCache(ReplayUser user) {
        this.user = user;
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayChunkData) {
            ReplayChunkData chunkData = (ReplayChunkData) packet;
            if (chunkData.getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
                chunkData.setLightData(this.lightQueue.remove(chunkData.getChunkPos())); // allowed to be null
            }
            this.chunks.put(chunkData.getChunkPos(), new ChunkWrapper(this.user, chunkData));
        } else if (packet instanceof ReplayChunkBulkData) {
            ReplayChunkBulkData chunkDataBulk = (ReplayChunkBulkData) packet;
            for (ReplayChunkData chunkData : chunkDataBulk.getChunks()) {
                this.chunks.put(chunkData.getChunkPos(), new ChunkWrapper(this.user, chunkData));
            }
        } else if (packet instanceof ReplayUpdateLightData) {
            ReplayUpdateLightData lightData = (ReplayUpdateLightData) packet;
            IChunkWrapper wrapper = this.chunks.get(lightData.getChunkPos());
            if (wrapper != null) {
                wrapper.setLightData(lightData.getLightData());
            } else if (lightData.getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
                LightData oldLight = this.lightQueue.put(lightData.getChunkPos(), lightData.getLightData());
                if (oldLight != null && lightData.getServerVersion().isOlderThan(ServerVersion.V_1_17)) {
                    LightDataUtils.appendLightData(lightData.getLightData(), oldLight);
                }
            }
        } else if (packet instanceof ReplayUpdateMultipleBlock) {
            ReplayUpdateMultipleBlock blocks = (ReplayUpdateMultipleBlock) packet;
            for (ReplayUpdateMultipleBlock.BlockClazz clazz : blocks.getBlocks()) {
                IChunkWrapper wrapper = this.chunks.get(clazz.getChunkPos());
                if (wrapper != null) {
                    wrapper.setBlock(clazz.getPos(), clazz.getBlockId());
                }
            }
        } else if (packet instanceof ReplayUpdateBlock) {
            ReplayUpdateBlock block = (ReplayUpdateBlock) packet;
            IChunkWrapper wrapper = this.chunks.get(block.getChunkPos());
            if (wrapper != null) {
                wrapper.setBlock(block.getBlockPos(), block.getBlockId());
            }
        } else if (packet instanceof ReplayUnloadChunkData) {
            ReplayUnloadChunkData chunkData = (ReplayUnloadChunkData) packet;
            this.chunks.remove(new ChunkPos(chunkData.getX(), chunkData.getZ()));
        }
    }

    public void clearCache() {
        this.chunks.clear();
    }

    public static class ChunkWrapper implements IChunkWrapper {
        private final ReplayUser user;
        private final ReplayChunkData chunk;

        public ChunkWrapper(ReplayUser user, ReplayChunkData chunk) {
            this.user = user;
            this.chunk = chunk;
        }

        @Override
        public Column getColumn() {
            return chunk.getColumn();
        }

        @Override
        public void setLightData(LightData lightData) {
            if (this.chunk.getLightData() == null || this.chunk.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) { // v 1.17+
                this.chunk.setLightData(lightData);
                return;
            }
            LightDataUtils.appendLightData(this.chunk.getLightData(), lightData); //TODO: Really this is need?
        }

        @Override
        public void setBlock(Vector3i pos, int blockId) {
            BaseChunk[] baseChunks = chunk.getColumn().getChunks();
            int y = (pos.getY() - this.user.getMinHeight()) >> 4; // Not null
            if (baseChunks.length <= y || y < 0)
                return;
            BaseChunk baseChunk = baseChunks[y];
            if (baseChunk == null) {
                baseChunk = BaseChunk.create();
            }
            baseChunk.set(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, blockId);
        }

        @Override
        public ReplayChunkData toPacket() {
            return chunk;
        }
    }

    public interface IChunkWrapper {
        Column getColumn();

        void setLightData(LightData lightData);

        void setBlock(Vector3i pos, int blockId);

        ReplayChunkData toPacket();
    }

    public static class ChunkPos {
        private final int x, z;

        public ChunkPos(int x, int z) {
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
            if (!(o instanceof ChunkPos)) return false;
            ChunkPos chunkPos = (ChunkPos) o;
            return x == chunkPos.x && z == chunkPos.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }

        @Override
        public String toString() {
            return "ChunkPos{" +
                    "x=" + x +
                    ", z=" + z +
                    '}';
        }
    }

    public void sentPacket(ReplayUser user) {

    }

    public List<ReplayChunkData> toPacket() {
        return chunks.values().stream()
                .map(IChunkWrapper::toPacket)
                .collect(Collectors.toList());
    }
}