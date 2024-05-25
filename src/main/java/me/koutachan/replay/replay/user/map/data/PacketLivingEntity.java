package me.koutachan.replay.replay.user.map.data;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.ReplayUser;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class PacketLivingEntity extends PacketEntity {
    protected List<EntityData> entityData;
    //protected float headPitch;

    public PacketLivingEntity(ReplayUser user, WrapperPlayServerSpawnLivingEntity living) {
        super(user, living.getEntityId(), living.getEntityUUID(), living.getEntityType()
                , new Location(living.getPosition().getX(), living.getPosition().getY(), living.getPosition().getZ(), living.getYaw(), living.getPitch())
                , living.getHeadPitch(), 0, living.getVelocity()
        );
        this.entityData = living.getEntityMetadata();
    }

    public void onEntityMetaUpdate(List<EntityData> entityData) {
        this.entityData = entityData;
    }

    /*public void onHeadPitch(float pitch) {
        headPitch = pitch;
    }*/

    @Override
    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = new ArrayList<>();
        Vector3d velocity = Vector3d.zero();
        if (!flag && VELOCITY_FETCH) {
            Entity entity = fromId(entityId);
            // I don't want to track velocity data.
            // Also, I don't think anyone would be foolish enough to try to set Velocity without registering with Bukkit...
            this.location = new Location(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch());
            velocity = toVector3d(entity.getVelocity());
        }
        packets.add(new ReplayPacketImpl(new WrapperPlayServerSpawnLivingEntity(entityId, uuid, entityType, location, headYaw, velocity, entityData)));
        if (!equipment.isEmpty()) {
            packets.add(new ReplayPacketImpl(new WrapperPlayServerEntityEquipment(entityId, equipment)));
        }
        return packets;
    }
}