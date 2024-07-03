package me.koutachan.replay.replay.user.cache;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.util.Vector3i;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.utils.LightDataQueue;
import me.koutachan.replay.utils.LightDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChunkCache {
    private final Map<ChunkPos, IChunkWrapper> cacheChunks = new HashMap<>();
    private final ReplayUser user;
    private final LightDataQueue lightQueue = new LightDataQueue();

    public ChunkCache(ReplayUser user) {
        this.user = user;
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayChunkData) {
            ReplayChunkData chunk = this.lightQueue.newChunk((ReplayChunkData) packet);
            this.cacheChunks.put(chunk.getChunkPos(), new ChunkWrapper(this.user, chunk));
        } else if (packet instanceof ReplayChunkBulkData) {
            for (ReplayChunkData chunk : ((ReplayChunkBulkData) packet).getChunks()) {
                this.cacheChunks.put(chunk.getChunkPos(), new ChunkWrapper(this.user, chunk));
            }
        } else if (packet instanceof ReplayUpdateLightData) {
            ReplayUpdateLightData lightData = (ReplayUpdateLightData) packet;
            IChunkWrapper chunk = this.cacheChunks.get(lightData.getChunkPos());
            if (chunk != null) {
                chunk.setLightData(lightData.getLightData());
            } else {
                this.lightQueue.newLight(lightData);
            }
        } else if (packet instanceof ReplayUpdateBlock) {
            ReplayUpdateBlock block = (ReplayUpdateBlock) packet;
            IChunkWrapper wrapper = this.cacheChunks.get(block.getChunkPos());
            if (wrapper != null) {
                wrapper.setBlock(block.getBlockPos(), block.getBlockId());
            }
        } else if (packet instanceof ReplayUpdateMultipleBlock) {
            for (ReplayUpdateMultipleBlock.BlockClazz clazz : ((ReplayUpdateMultipleBlock) packet).getBlocks()) {
                IChunkWrapper wrapper = this.cacheChunks.get(clazz.getChunkPos());
                if (wrapper != null) {
                    wrapper.setBlock(clazz.getPos(), clazz.getBlockId());
                }
            }
        } else if (packet instanceof ReplayUnloadChunkData) {
            this.cacheChunks.remove(((ReplayUnloadChunkData) packet).getChunkPos());
        }
    }

    public void clearCache() {
        this.cacheChunks.clear();
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
        public LightData getLightData() {
            return chunk.getLightData();
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

        LightData getLightData();

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
        return cacheChunks.values().stream()
                .map(IChunkWrapper::toPacket)
                .collect(Collectors.toList());
    }
}