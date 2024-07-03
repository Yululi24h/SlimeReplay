package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.cache.ChunkCache;

import java.util.List;

public class ReplayUpdateLightData extends ReplayWrapper<ReplayUpdateLightData> {
    private int x;
    private int z;
    private LightData lightData;

    public ReplayUpdateLightData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayUpdateLightData(PacketSendEvent event) {
        WrapperPlayServerUpdateLight wrapper = new WrapperPlayServerUpdateLight(event);
        this.x = wrapper.getX();
        this.z = wrapper.getZ();
        this.lightData = wrapper.getLightData();
    }

    public ReplayUpdateLightData(int x, int z, LightData lightData) {
        this.x = x;
        this.z = z;
        this.lightData = lightData;
    }

    @Override
    public void read() {
        this.x = readVarInt();
        this.z = readVarInt();
        this.lightData = new LightData();
        this.lightData.read(this);
    }

    @Override
    public void write() {
        writeVarInt(this.x);
        writeVarInt(this.z);
        this.lightData.write(this);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public ChunkCache.ChunkPos getChunkPos() {
        return new ChunkCache.ChunkPos(this.x, this.z);
    }

    public LightData getLightData() {
        return lightData;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
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
