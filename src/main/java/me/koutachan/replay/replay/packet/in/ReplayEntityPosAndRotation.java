package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityPosAndRotation extends ReplayWrapper<ReplayEntityPosAndRotation> {
    protected int entityId;
    protected Location location;
    protected boolean onGround;

    public ReplayEntityPosAndRotation(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityPosAndRotation() {

    }

    public ReplayEntityPosAndRotation(PacketSendEvent event) {
        WrapperPlayServerEntityRelativeMoveAndRotation relative = new WrapperPlayServerEntityRelativeMoveAndRotation(event);
        this.location = new Location(
                relative.getDeltaX(),
                relative.getDeltaY(),
                relative.getDeltaZ(),
                relative.getYaw(),
                relative.getPitch()
        ); // Delta
        this.onGround = relative.isOnGround();
    }

    public ReplayEntityPosAndRotation(int entityId, Location location) {
        super();
        this.entityId = entityId;
        this.location = location;
    }

    public ReplayEntityPosAndRotation(int entityId, Location location, boolean onGround) {
        this.entityId = entityId;
        this.location = location;
        this.onGround = onGround;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.location = new Location(
                readDouble(),
                readDouble(),
                readDouble(),
                readFloat(),
                readFloat()
        );
        this.onGround = readBoolean();
    }

    @Override
    public void write() {
        writeInt(this.entityId);
        writeDouble(this.location.getX());
        writeDouble(this.location.getY());
        writeDouble(this.location.getZ());
        writeFloat(this.location.getYaw());
        writeFloat(this.location.getPitch());
        writeBoolean(this.onGround);
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getLocation() {
        return location;
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    public float getYaw() {
        return location.getYaw();
    }

    public float getPitch() {
        return location.getPitch();
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityRelativeMoveAndRotation(
                this.entityId,
                this.location.getX(),
                this.location.getY(),
                this.location.getZ(),
                this.location.getYaw(),
                this.location.getPitch(),
                this.onGround
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}