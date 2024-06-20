package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;

import java.util.List;
import java.util.UUID;

public class ReplayLivingEntitySpawnData extends ReplayWrapper<ReplayLivingEntitySpawnData> {
    public static UUID EMPTY_UUID = new UUID(0L, 0L);

    private int entityId;
    private EntityType entityType;
    private UUID uuid;
    private Location location;
    private float headPitch;
    private Vector3d velocity;
    private List<EntityData> entityData;

    public ReplayLivingEntitySpawnData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayLivingEntitySpawnData(PacketSendEvent event) {
        switch ((PacketType.Play.Server) event.getPacketType()) {
            case SPAWN_ENTITY: {
                WrapperPlayServerSpawnEntity entity = new WrapperPlayServerSpawnEntity(event);
                this.entityId = entity.getEntityId();
                this.entityType = entity.getEntityType();
                this.uuid = entity.getUUID().orElse(EMPTY_UUID);
                this.location = new Location(
                        entity.getPosition().getX(),
                        entity.getPosition().getY(),
                        entity.getPosition().getZ(),
                        entity.getYaw(),
                        entity.getPitch()
                );
                this.velocity = entity.getVelocity().orElse(new Vector3d(-1.0D, -1.0D, -1.0D));
                break;
            }
            case SPAWN_LIVING_ENTITY: {
                WrapperPlayServerSpawnLivingEntity entity = new WrapperPlayServerSpawnLivingEntity(event);
                this.entityId = entity.getEntityId();
                this.entityType = entity.getEntityType();
                this.uuid = EMPTY_UUID;
                this.location = new Location(
                        entity.getPosition().getX(),
                        entity.getPosition().getY(),
                        entity.getPosition().getZ(),
                        entity.getYaw(),
                        entity.getPitch()
                );
                this.velocity = entity.getVelocity();
                this.headPitch = entity.getHeadPitch(); //TODO: what is head pitch?
                this.entityData = entity.getEntityMetadata();
                break;
            }
        }
    }

    public ReplayLivingEntitySpawnData(int entityId, EntityType entityType, UUID uuid, Location location, float headYaw, Vector3d velocity, List<EntityData> entityData) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.uuid = uuid;
        this.location = location;
        this.headPitch = headYaw;
        this.velocity = velocity;
        this.entityData = entityData;
    }

    @Override
    public void read() {
        this.entityId = readInt();
        this.entityType = readMappedEntity(EntityTypes::getById);
        this.uuid = readUUID();
        this.location = new Location(
                readDouble(),
                readDouble(),
                readDouble(),
                readFloat(),
                readFloat()
        );
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
        writeInt(this.entityId);
        writeMappedEntity(this.entityType);
        writeUUID(this.uuid);
        writeDouble(this.location.getX());
        writeDouble(this.location.getY());
        writeDouble(this.location.getZ());
        writeFloat(this.location.getYaw());
        writeFloat(this.location.getPitch());
        writeFloat(this.headPitch);
        writeDouble(this.velocity.getX());
        writeDouble(this.velocity.getY());
        writeDouble(this.velocity.getZ());
        writeEntityMetadata(this.entityData);
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isUUIDUndefined() {
        return EMPTY_UUID == this.uuid;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
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
