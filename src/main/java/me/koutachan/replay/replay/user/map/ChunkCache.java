package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.util.Vector3i;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.utils.LightDataUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ChunkCache {
    private final Map<ChunkPos, IChunkWrapper> chunks = new HashMap<>();
    private final ReplayUser user;

    private ReplayUpdateLightData lastLightData;

    public ChunkCache(ReplayUser user) {
        this.user = user;
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayChunkData) {
            ReplayChunkData chunkData = (ReplayChunkData) packet;
            if (this.lastLightData != null) { // Minecraft will send light data ahead...
                if (chunkData.getX() == this.lastLightData.getX() && chunkData.getZ() == this.lastLightData.getZ()) {
                    chunkData.setLightData(this.lastLightData.getLightData());
                    System.out.println("Congrats! we replaced lightdata!");
                } else {
                    System.out.println("Not = " + this.lastLightData.getChunkPos() + " chunk=" + chunkData.toChunkPos());
                }

                this.lastLightData = null;

            }

            this.chunks.put(chunkData.toChunkPos(), new ChunkWrapper(chunkData));


        } else if (packet instanceof ReplayChunkBulkData) {
            ReplayChunkBulkData chunkDataBulk = (ReplayChunkBulkData) packet;
            for (ReplayChunkData chunkData : chunkDataBulk.getChunks()) {
                this.chunks.put(new ChunkPos(chunkData.getX(), chunkData.getZ()), new ChunkWrapper(chunkData));
            }
        } else if (packet instanceof ReplayUpdateLightData) {
            ReplayUpdateLightData lightData = (ReplayUpdateLightData) packet;
            IChunkWrapper wrapper = this.chunks.get(lightData.getChunkPos());
            if (wrapper != null) {
                wrapper.setLightData(lightData.getLightData());
            } else {
                this.lastLightData = lightData; // Minecraft will send light data ahead...
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
        System.err.println("== Chunk cleared! ==");
        this.chunks.clear();
    }

    public static class ChunkWrapper implements IChunkWrapper {
        private final ReplayChunkData chunk;

        public ChunkWrapper(ReplayChunkData chunk) {
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
            LightDataUtils.appendLightData(this.chunk.getLightData(), lightData);
        }

        @Override
        public void setBlock(Vector3i pos, int blockId) {
            BaseChunk[] baseChunks = chunk.getColumn().getChunks();
            int y = pos.getY() >> 4;
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