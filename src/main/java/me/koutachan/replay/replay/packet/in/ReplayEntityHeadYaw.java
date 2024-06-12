package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityHeadYaw extends ReplayWrapper<ReplayEntityHeadYaw> {
    private int entityId;
    private float headYaw;

    public ReplayEntityHeadYaw(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityHeadYaw(PacketSendEvent event) {
        WrapperPlayServerEntityHeadLook wrapper = new WrapperPlayServerEntityHeadLook(event);
        this.entityId = wrapper.getEntityId();
        this.headYaw = wrapper.getHeadYaw();
    }

    public ReplayEntityHeadYaw(int entityId, float headYaw) {
        this.entityId = entityId;
        this.headYaw = headYaw;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.headYaw = readFloat();
    }

    @Override
    public void write() {
        writeVarInt(this.entityId);
        writeFloat(this.headYaw);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityHeadLook(
                this.entityId,
                this.headYaw
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
