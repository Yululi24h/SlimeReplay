package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnPlayer;

import java.util.List;

public class ReplaySpawnPlayer extends ReplayEntityAbstract {
    private float yaw;
    private float pitch;
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
    public void setEntityMeta(List<EntityData> entityData) {
        this.entityData = entityData;
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
