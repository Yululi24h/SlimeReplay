package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReplaySpawnEntityData extends ReplayEntityAbstract{
    protected EntityType entityType;
    protected float yaw;
    protected float pitch;
    protected float headYaw;
    private Vector3d velocity;
    private int data;
    private List<EntityData> entityData;

    public ReplaySpawnEntityData() {

    }

    @Override
    public void read() {
        super.read();
        this.entityType = EntityTypes.getById(this.serverVersion.toClientVersion(), readVarInt());
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
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        //int entityID, Optional<UUID> uuid, EntityType entityType, Vector3d position, float pitch, float yaw, float headYaw, int data, Optional<Vector3d> velocity
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
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
