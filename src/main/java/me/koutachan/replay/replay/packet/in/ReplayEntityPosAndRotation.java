package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityPosAndRotation extends ReplayWrapper<ReplayEntityPosAndRotation> {
    private int entityId;
    private Location location;
    private boolean onGround;

    public ReplayEntityPosAndRotation(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    public ReplayEntityPosAndRotation(int entityId, Location location) {
        super();
        this.entityId = entityId;
        this.location = location;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.location = new Location(readDouble(), readDouble(), readDouble(), readFloat(), readFloat());
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

    @Override
    public List<PacketWrapper<?>> getPacket() {
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
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
