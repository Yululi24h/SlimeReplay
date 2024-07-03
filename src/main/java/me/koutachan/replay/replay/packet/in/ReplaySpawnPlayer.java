package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReplaySpawnPlayer extends ReplayEntityAbstract {
    private float yaw;
    private float pitch;
    private UUID uuid;
    private List<EntityData> entityData;

    public ReplaySpawnPlayer(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplaySpawnPlayer(PacketSendEvent event) {
        WrapperPlayServerSpawnPlayer wrapper = new WrapperPlayServerSpawnPlayer(event);
        this.classType = ClassType.PLAYER;
        this.entityId = wrapper.getEntityId();
        this.position = wrapper.getPosition();
        this.yaw = wrapper.getYaw();
        this.pitch = wrapper.getPitch();
        this.entityData = wrapper.getEntityMetadata(); // Just calf
    }

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
        this.uuid = readUUID();
        this.entityData = readEntityMetadata();
    }

    @Override
    public void write() {
        super.write();
        writeFloat(this.yaw);
        writeFloat(this.pitch);
        writeUUID(this.uuid);
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
        packets.add(new WrapperPlayServerSpawnPlayer(
                this.entityId,
                this.uuid,
                this.position,
                this.yaw,
                this.pitch,
                this.entityData
        ));
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_15)) {
            packets.add(new WrapperPlayServerEntityMetadata(this.entityId, this.entityData));
        }
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}