package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;

public abstract class ReplayEntityAbstract extends ReplayWrapper<ReplayEntityAbstract>{
    protected int entityId;
    protected Vector3d position;

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public void read() {
        this.entityId = readVarInt();
        this.position = new Vector3d(
                readDouble(),
                readDouble(),
                readDouble()
        );
    }

    @Override
    public void write() {
        writeVarInt(this.entityId);
        writeDouble(this.position.getX());
        writeDouble(this.position.getY());
        writeDouble(this.position.getZ());
    }

    public int getEntityId() {
        return entityId;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public abstract Location getLocation();

    public abstract void setLocation(Location location);
}
