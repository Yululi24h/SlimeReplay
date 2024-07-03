package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import me.koutachan.replay.replay.user.cache.ChunkCache;

import java.util.ArrayList;
import java.util.List;

public class ReplayUpdateMultipleBlock extends ReplayWrapper<ReplayUpdateMultipleBlock> {
    private Vector3i chunkPosition;
    private Boolean trustEdges;
    private List<BlockClazz> blocks;

    public ReplayUpdateMultipleBlock(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayUpdateMultipleBlock(Vector3i chunkPosition, Boolean trustEdges, List<BlockClazz> blocks) {
        this.chunkPosition = chunkPosition;
        this.trustEdges = trustEdges;
        this.blocks = blocks;
    }

    @Override
    public void read() {
        this.chunkPosition = readBlockPosition();
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_16) && serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            this.trustEdges = readBoolean();
        }
        this.blocks = new ArrayList<>();
        for (int i = 0; i < readVarInt(); i++) {
            this.blocks.add(new BlockClazz(readBlockPosition(), readVarInt()));
        }
    }

    @Override
    public void write() {
        writeBlockPosition(this.chunkPosition);
        if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_16) && serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
            writeBoolean(this.trustEdges);
        }
        writeVarInt(this.blocks.size());
        for (BlockClazz clazz : this.blocks) {
            writeBlockPosition(clazz.getPos());
            writeVarInt(clazz.getBlockId());
        }
    }

    public Vector3i getChunkPosition() {
        return chunkPosition;
    }

    public Boolean getTrustEdges() {
        return trustEdges;
    }

    public List<BlockClazz> getBlocks() {
        return blocks;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        WrapperPlayServerMultiBlockChange.EncodedBlock[] blockData = new WrapperPlayServerMultiBlockChange.EncodedBlock[this.blocks.size()];
        for (int i = 0; i < blockData.length; i++) {
            BlockClazz clazz = this.blocks.get(i);
            blockData[i] = new WrapperPlayServerMultiBlockChange.EncodedBlock(
                    clazz.getPos(),
                    clazz.getBlockId()
            );
        }
        packets.add(new WrapperPlayServerMultiBlockChange(
                this.chunkPosition,
                this.trustEdges,
                blockData
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }

    public static class BlockClazz {
        private final Vector3i pos;
        private final int blockId;

        public BlockClazz(Vector3i pos, int blockId) {
            this.pos = pos;
            this.blockId = blockId;
        }

        public ChunkCache.ChunkPos getChunkPos() {
            return new ChunkCache.ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        }

        public Vector3i getPos() {
            return pos;
        }

        public int getBlockId() {
            return blockId;
        }
    }
}
