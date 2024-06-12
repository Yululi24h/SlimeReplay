package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityVelocity extends ReplayWrapper<ReplayEntityVelocity> {
    private int entityId;
    private Vector3d velocity;

    public ReplayEntityVelocity(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityVelocity(PacketSendEvent event) {
        WrapperPlayServerEntityVelocity wrapper = new WrapperPlayServerEntityVelocity(event);
        this.entityId = wrapper.getEntityId();
        this.velocity = wrapper.getVelocity();
    }

    public ReplayEntityVelocity(int entityId, Vector3d velocity) {
        this.entityId = entityId;
        this.velocity = velocity;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.velocity = new Vector3d(readDouble(), readDouble(), readDouble());
    }

    @Override
    public void write() {
        writeInt(this.entityId);
        writeDouble(this.velocity.getX());
        writeDouble(this.velocity.getY());
        writeDouble(this.velocity.getZ());
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityVelocity(
                this.entityId,
                this.velocity
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }
}