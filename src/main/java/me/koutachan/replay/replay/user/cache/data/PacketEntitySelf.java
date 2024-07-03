package me.koutachan.replay.replay.user.cache.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.packet.in.ReplaySpawnPlayer;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.UUID;

public class PacketEntitySelf extends PacketEntity {
    public PacketEntitySelf(ReplayUser user, int entityId, UUID uuid, EntityType entityType, Location location, float headYaw, int data, Vector3d velocity) {
        super(user, entityId, uuid, entityType, location, headYaw, data, velocity);
    }

    @Override
    public void onMove(double deltaX, double deltaY, double deltaZ) {
        this.location = new Location(deltaX, deltaY, deltaZ, this.location.getYaw(), this.location.getPitch());
    }

    @Override
    public ReplayEntityAbstract toPacket() {
        return new ReplaySpawnPlayer(
                this.entityId,
                this.location,
                this.entityData
        );
    }

    public Location getLocation() {
        return this.location;
    }
}