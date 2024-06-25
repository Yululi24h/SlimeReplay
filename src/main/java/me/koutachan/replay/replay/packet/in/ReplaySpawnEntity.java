package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReplaySpawnEntity extends ReplayEntityAbstract {
    protected EntityType entityType;
    protected float yaw;
    protected float pitch;
    protected float headYaw;
    private Vector3d velocity;
    private int data;
    private List<EntityData> entityData;

    public ReplaySpawnEntity(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplaySpawnEntity(int entityId, EntityType entityType, Location location, float headYaw, Vector3d velocity, int data, List<EntityData> entityData) {
        super(ClassType.OBJECT, entityId, location.getPosition());
        this.entityType = entityType;
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.headYaw = headYaw;
        this.velocity = velocity;
        this.data = data;
        this.entityData = entityData;
    }

    @Override
    public void read() {
        super.read();
        this.entityType = EntityTypes.getById(this.serverVersion.toClientVersion(), readVarInt());
        this.yaw = readFloat();
        this.pitch = readFloat();
        this.headYaw = readFloat();
        this.velocity = new Vector3d(
                readDouble(),
                readDouble(),
                readDouble()
        );
        this.data = readVarInt();
        this.entityData = readEntityMetadata();
    }

    @Override
    public void write() {
        super.write();
        writeVarInt(this.entityType.getId(this.serverVersion.toClientVersion()));
        writeFloat(this.yaw);
        writeFloat(this.pitch);
        writeFloat(this.headYaw);
        writeDouble(this.velocity.getX());
        writeDouble(this.velocity.getY());
        writeDouble(this.velocity.getZ());
        writeVarInt(this.data);
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
    public void setEntityMeta(List<EntityData> entityData) {
        this.entityData = entityData;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerSpawnEntity(
                this.entityId,
                Optional.of(UUID.randomUUID()),
                this.entityType,
                this.position,
                this.pitch,
                this.yaw,
                this.headYaw,
                this.data,
                Optional.of(this.velocity)
        ));
        if (this.serverVersion.isOlderThan(ServerVersion.V_1_9)) {
            packets.add(new WrapperPlayServerEntityVelocity(this.entityId, this.velocity));
        }
        if (!this.entityData.isEmpty()) {
            packets.add(new WrapperPlayServerEntityMetadata(this.entityId, this.entityData));
        }
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}