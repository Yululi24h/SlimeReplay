package me.koutachan.replay.replay.user.cache.data;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.world.Location;
import me.koutachan.replay.replay.packet.in.ReplayEntityAbstract;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.List;

public abstract class BasePacketEntity {
    protected final ReplayUser user;
    protected final int entityId;

    public BasePacketEntity(ReplayUser user, int entityId) {
        this.user = user;
        this.entityId = entityId;
    }

    public void onVelocity() {

    }

    public void onMove(double deltaX, double deltaY, double deltaZ) {

    }

    public void onRotation(float yaw, float pitch) {

    }

    public void onPosition(Location location) {

    }

    public void onHeadYaw(float yaw) {

    }

    public void onEntityMetaUpdate(List<EntityData> data) {

    }

    public void onEquipmentUpdate(List<Equipment> equipment) {

    }

    public ReplayUser getUser() {
        return user;
    }

    public int getEntityId() {
        return entityId;
    }

    public abstract ReplayEntityAbstract toPacket();
}