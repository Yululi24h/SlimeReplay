package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.List;

public class ReplayPlayerTeleportData extends ReplayWrapper<ReplayPlayerTeleportData> {
    private int entityId;
    private Location location;
    private int teleportId;

    public ReplayPlayerTeleportData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayPlayerTeleportData(PacketSendEvent event) {
        WrapperPlayServerPlayerPositionAndLook positionAndLook = new WrapperPlayServerPlayerPositionAndLook(event);
        this.entityId = event.getUser().getEntityId();
        this.location = new Location(
                positionAndLook.getX(),
                positionAndLook.getY(),
                positionAndLook.getZ(),
                positionAndLook.getYaw(),
                positionAndLook.getPitch()
        );
        this.teleportId = positionAndLook.getTeleportId();
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
    }

    @Override
    public void write() {
        writeInt(this.entityId);
        writeDouble(this.location.getX());
        writeDouble(this.location.getY());
        writeDouble(this.location.getZ());
        writeFloat(this.location.getYaw());
        writeFloat(this.location.getPitch());
    }

    public int getTeleportId() {
        return teleportId;
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