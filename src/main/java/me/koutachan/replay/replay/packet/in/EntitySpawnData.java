package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.List;
import java.util.UUID;

public class EntitySpawnData extends ReplayWrapper<EntitySpawnData> {
    private int entityId;
    private EntityType entityType;
    private UUID uuid;
    private Location location;
    private float headYaw;
    private Vector3d velocity;
    private List<EntityData> entityData;

    public EntitySpawnData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public EntitySpawnData(int entityId, EntityType entityType, UUID uuid, Location location, float headYaw, Vector3d velocity, List<EntityData> entityData) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.uuid = uuid;
        this.location = location;
        this.headYaw = headYaw;
        this.velocity = velocity;
        this.entityData = entityData;
    }

    @Override
    public void read() {
        super.read();
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
        this.headYaw = readFloat();
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
        writeFloat(this.headYaw);
        writeDouble(this.velocity.getX());
        writeDouble(this.velocity.getY());
        writeDouble(this.velocity.getZ());
        writeEntityMetadata(this.entityData);
    }

    public boolean isUUIDUndefined() {
        return this.uuid.getLeastSignificantBits() == 0 && this.uuid.getMostSignificantBits() == 0;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
