package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import me.koutachan.replay.replay.user.map.ChunkMap;

import java.util.ArrayList;
import java.util.List;

public class ReplayUpdateBlock extends ReplayWrapper<ReplayUpdateBlock> {
    private Vector3i blockPos;
    private int blockId;

    public ReplayUpdateBlock(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayUpdateBlock(PacketSendEvent event) {
        WrapperPlayServerBlockChange wrapper = new WrapperPlayServerBlockChange(event);
        this.blockPos = wrapper.getBlockPosition();
        this.blockId = wrapper.getBlockId();
    }

    @Override
    public void read() {
        this.blockPos = readBlockPosition();
        this.blockId = readVarInt();
    }

    @Override
    public void write() {
        writeBlockPosition(this.blockPos);
        writeVarInt(this.blockId);
    }

    public int getX() {
        return blockPos.getX();
    }

    public int getY() {
        return blockPos.getY();
    }

    public int getZ() {
        return blockPos.getZ();
    }

    public Vector3i getBlockPos() {
        return blockPos;
    }

    public int getBlockId() {
        return blockId;
    }

    public ChunkMap.ChunkPos getChunkPos() {
        return new ChunkMap.ChunkPos(getX() >> 4, getZ() >> 4);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>(1);
        packets.add(new WrapperPlayServerBlockChange(
                this.blockPos,
                this.blockId
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
