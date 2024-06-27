package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.data.BasePacketEntity;
import me.koutachan.replay.replay.user.map.data.PacketEntitySelf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntitiesCache {
    private PacketEntitySelf self;
    private final Map<Integer, BasePacketEntity> entities = new HashMap<>();
    private final ReplayUser user;

    public EntitiesCache(ReplayUser user) {
        this.user = user;
        this.self = new PacketEntitySelf(
                this.user,
                this.user.getEntityId(),
                this.user.getUser().getUUID(),
                EntityTypes.PLAYER,
                SpigotConversionUtil.fromBukkitLocation(this.user.getPlayer().getLocation()),
                0F,
                0,
                Vector3d.zero()
        );
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayEntityPos) {
            ReplayEntityPos entityPos = (ReplayEntityPos) packet;
            BasePacketEntity entity = getEntity(entityPos.getEntityId());
            if (entity != null) {
                entity.onMove(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            }
        } else if (packet instanceof ReplayEntityVelocity) {
            ReplayEntityVelocity entityVelocity = (ReplayEntityVelocity) packet;
            BasePacketEntity entity = getEntity(entityVelocity.getEntityId());
            if (entity != null) {
                entity.onVelocity();
            }
        } else if (packet instanceof ReplayEntityRotation) {
            ReplayEntityRotation entityRotation = (ReplayEntityRotation) packet;
            BasePacketEntity entity = getEntity(entityRotation.getEntityId());
            if (entity != null) {
                entity.onRotation(entityRotation.getYaw(), entityRotation.getPitch());
            }
        } else if (packet instanceof ReplayEntityTeleport) {
            ReplayEntityTeleport entityTeleport = (ReplayEntityTeleport) packet;
            BasePacketEntity entity = getEntity(entityTeleport.getEntityId());
            if (entity != null) {
                entity.onPosition(entityTeleport.getLocation());
            }
        } else if (packet instanceof ReplayEntityPosAndRotation) {
            ReplayEntityPosAndRotation entityPosAndRotation = (ReplayEntityPosAndRotation) packet;
            BasePacketEntity entity = getEntity(entityPosAndRotation.getEntityId());
            if (entity != null) {
                entity.onMove(entityPosAndRotation.getX(), entityPosAndRotation.getY(), entityPosAndRotation.getZ());
                entity.onRotation(entityPosAndRotation.getYaw(), entityPosAndRotation.getPitch());
            }
        } else if (packet instanceof ReplayEntityHeadYaw) {
            ReplayEntityHeadYaw entityHeadYaw = (ReplayEntityHeadYaw) packet;
            BasePacketEntity entity = getEntity(entityHeadYaw.getEntityId());
            if (entity != null) {
                entity.onHeadYaw(entityHeadYaw.getHeadYaw());
            }
        } else if (packet instanceof ReplayUpdateEntityData) {
            ReplayUpdateEntityData entityData = (ReplayUpdateEntityData) packet;
            BasePacketEntity entity = getEntity(entityData.getEntityId());
            if (entity != null) {
                entity.onEntityMetaUpdate(entityData.getEntityData());
            }
        } else if (packet instanceof ReplayEntityEquipment) {
            ReplayEntityEquipment entityEquipment = (ReplayEntityEquipment) packet;
            BasePacketEntity entity = getEntity(entityEquipment.getEntityId());
            if (entity != null) {
                entity.onEquipmentUpdate(entityEquipment.getEquipments());
            }
        } else if (packet instanceof ReplayDestroyEntities) {
            ReplayDestroyEntities destroyEntities = (ReplayDestroyEntities) packet;
            for (int entityId : destroyEntities.getEntityIds()) {
                this.entities.remove(entityId);
            }
        }
    }

    public BasePacketEntity getEntity(int entityId) {
        if (entityId == this.self.getEntityId()) {
            return this.self;
        }
        return this.entities.get(entityId);
    }

    public void clearCache() {
        this.entities.clear();
    }

    public List<ReplayEntityAbstract> toPacket() {
        return this.entities.values()
                .stream()
                .map(BasePacketEntity::toPacket)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}