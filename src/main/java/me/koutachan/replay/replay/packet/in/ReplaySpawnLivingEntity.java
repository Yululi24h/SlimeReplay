package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;

public class ReplaySpawnLivingEntity extends ReplayEntityAbstract {
    private EntityType entityType;
    private float yaw;
    private float pitch;
    private float headPitch;
    // private float headYaw; //TODO:
    private Vector3d velocity;
    private List<EntityData> entityData;

    public ReplaySpawnLivingEntity(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    @Override
    public void read() {
        super.read();
        this.entityType = EntityTypes.getById(this.serverVersion.toClientVersion(), readVarInt());
        this.yaw = readFloat();
        this.pitch = readFloat();
        this.headPitch = readFloat();
        this.velocity = new Vector3d(
                readDouble(),
                readDouble(),
                readDouble()
        );
        this.entityData = readEntityMetadata();
    }

    @Override
    public void write() {
        super.write();
        writeVarInt(this.entityType.getId(this.serverVersion.toClientVersion()));
        writeFloat(this.yaw);
        writeFloat(this.pitch);
        writeFloat(this.headPitch);
        writeDouble(this.velocity.getX());
        writeDouble(this.velocity.getY());
        writeDouble(this.velocity.getZ());
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