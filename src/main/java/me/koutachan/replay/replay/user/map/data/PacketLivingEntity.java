package me.koutachan.replay.replay.user.map.data;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;

public class PacketLivingEntity extends PacketEntity {
    //protected float headPitch;

    public PacketLivingEntity(ReplayUser user, WrapperPlayServerSpawnLivingEntity living) {
        //TODO: What is HeadPitch??
        super(user, living.getEntityId(), living.getEntityUUID(), living.getEntityType()
                , new Location(living.getPosition().getX(), living.getPosition().getY(), living.getPosition().getZ(), living.getYaw(), living.getPitch())
                , living.getHeadPitch(), 0, living.getVelocity()
        );
        this.entityData = living.getEntityMetadata();
    }

    @Override
    public ReplayEntityAbstract toPacket() {
        /*List<ReplayPacket> packets = new ArrayList<>();
        Vector3d velocity = Vector3d.zero();
        if (flag && VELOCITY_FETCH) {
            Entity entity = fromId(entityId);
            if (entity != null) {
                // I don't want to track velocity data.
                // Also, I don't think anyone would be foolish enough to try to set Velocity without registering with Bukkit...
                this.location = new Location(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ(), entity.getLocation().getYaw(), entity.getLocation().getPitch());
                velocity = toVector3d(entity.getVelocity());
            }
        }
        //packets.add(new ReplayPacketImpl(new ReplayLivingEntitySpawnData(entityId, uuid, entityType, location, headYaw, velocity, entityId)))//new ReplayPacketImpl(new WrapperPlayServerSpawnLivingEntity(entityId, uuid, entityType, location, headYaw, velocity, entityData)));
        return addEntityPacket(packets);*/
        return null;
    }
}