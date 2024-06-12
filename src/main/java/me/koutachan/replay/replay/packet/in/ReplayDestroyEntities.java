package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;

import java.util.List;

public class ReplayDestroyEntities extends ReplayWrapper<ReplayDestroyEntities> {
    private int[] entityIds;

    public ReplayDestroyEntities(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayDestroyEntities(PacketSendEvent event) {
        WrapperPlayServerDestroyEntities wrapper = new WrapperPlayServerDestroyEntities(event);
        this.entityIds = wrapper.getEntityIds();
    }

    public ReplayDestroyEntities(int[] entityId) {
        this.entityIds = entityId;
    }

    @Override
    public void read() {
        this.entityIds = readVarIntArray();
    }

    @Override
    public void write() {
        writeVarIntArray(this.entityIds);
    }

    public int[] getEntityIds() {
        return entityIds;
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
