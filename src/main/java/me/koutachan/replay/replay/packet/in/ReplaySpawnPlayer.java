package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;

public class ReplaySpawnPlayer extends ReplayEntityAbstract {
    private float yaw;
    private float pitch;
    private List<EntityData> entityData;

    public ReplaySpawnPlayer(int entityId, Location location, List<EntityData> entityData) {
        super(ClassType.PLAYER, entityId, location.getPosition());
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.entityData = entityData;
    }

    @Override
    public void read() {
        super.read();
        this.yaw = readFloat();
        this.pitch = readFloat();
        this.entityData = readEntityMetadata();
    }

    @Override
    public void write() {
        super.write();
        writeFloat(this.yaw);
        writeFloat(this.pitch);
        writeEntityMetadata(this.entityData);
    }

    @Override
    public Location getLocation() {
        return new Location(this.position, this.yaw, this.pitch);
    }

    @Override
    public void setLocation(Location location) {
        this.position = location.getPosition();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
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