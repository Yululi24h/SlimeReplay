package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import me.koutachan.replay.utils.ChunkPos;

import java.util.List;

public class ReplayUnloadChunkData extends ReplayWrapper<ReplayUnloadChunkData> {
    private int x;
    private int z;

    public ReplayUnloadChunkData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayUnloadChunkData(PacketSendEvent event) {
        WrapperPlayServerUnloadChunk chunk = new WrapperPlayServerUnloadChunk(event);
        this.x = chunk.getChunkX();
        this.z = chunk.getChunkZ();
    }

    public ReplayUnloadChunkData(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public void read() {
        this.x = readInt();
        this.z = readInt();
    }

    @Override
    public void write() {
        writeInt(this.x);
        writeInt(this.z);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos(this.x, this.z);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
