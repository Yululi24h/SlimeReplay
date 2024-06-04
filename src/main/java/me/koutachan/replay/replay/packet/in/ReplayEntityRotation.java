package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityRotation extends ReplayWrapper<ReplayEntityRotation> {
    private int entityId;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public ReplayEntityRotation(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityRotation(PacketSendEvent event) {
        WrapperPlayServerEntityRotation rotation = new WrapperPlayServerEntityRotation(event);
        this.entityId = rotation.getEntityId();
        this.yaw = rotation.getYaw();
        this.pitch = rotation.getPitch();
        this.onGround = rotation.isOnGround();
    }

    public ReplayEntityRotation(int entityId, float yaw, float pitch, boolean onGround) {
        super();
        this.entityId = entityId;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.yaw = readFloat();
        this.pitch = readFloat();
        this.onGround = readBoolean();
    }

    @Override
    public void write() {
        writeInt(this.entityId);
        writeFloat(this.yaw);
        writeFloat(this.pitch);
        writeBoolean(this.onGround);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityRotation(
                this.entityId,
                this.yaw,
                this.pitch,
                this.onGround
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
