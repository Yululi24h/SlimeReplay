package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.user.PacketMap;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.data.BasePacketEntity;
import me.koutachan.replay.replay.user.map.data.PacketEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntitiesCache {
    private final Map<Integer, BasePacketEntity> entities = new HashMap<>();
    private final ReplayUser user;

    public EntitiesCache(ReplayUser user) {
        this.user = user;
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayLivingEntitySpawnData) {
            ReplayLivingEntitySpawnData spawnData = (ReplayLivingEntitySpawnData) packet;
            entities.put(spawnData.getEntityId(), new PacketEntity(this.user, ))

        } else if (packet instanceof ReplayEntityPos) {
            ReplayEntityPos entityPos = (ReplayEntityPos) packet;
            BasePacketEntity entity = this.entities.get(entityPos.getEntityId());
            if (entity != null) {
                entity.onMove(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            }
        } else if (packet instanceof ReplayEntityVelocity) {
            ReplayEntityVelocity entityVelocity = (ReplayEntityVelocity) packet;
            BasePacketEntity entity = this.entities.get(entityVelocity.getEntityId());
            if (entity != null) {
                entity.onVelocity();
            }
        } else if (packet instanceof ReplayEntityRotation) {
            ReplayEntityRotation entityRotation = (ReplayEntityRotation) packet;
            BasePacketEntity entity = this.entities.get(entityRotation.getEntityId());
            if (entity != null) {
                entity.onRotation(entityRotation.getYaw(), entityRotation.getPitch());
            }
        } else if (packet instanceof ReplayEntityTeleport) {
            ReplayEntityTeleport entityTeleport = (ReplayEntityTeleport) packet;
            BasePacketEntity entity = this.entities.get(entityTeleport.getEntityId());
            if (entity != null) {
                entity.onPosition(entityTeleport.getLocation());
            }
        } else if (packet instanceof ReplayEntityPosAndRotation) {
            ReplayEntityPosAndRotation entityPosAndRotation = (ReplayEntityPosAndRotation) packet;
            BasePacketEntity entity = this.entities.get(entityPosAndRotation.getEntityId());
            if (entity != null) {
                entity.onMove(entityPosAndRotation.getX(), entityPosAndRotation.getY(), entityPosAndRotation.getZ());
                entity.onRotation(entityPosAndRotation.getYaw(), entityPosAndRotation.getPitch());
            }
        } else if (packet instanceof ReplayEntityHeadYaw) {
            ReplayEntityHeadYaw entityHeadYaw = (ReplayEntityHeadYaw) packet;
            BasePacketEntity entity = this.entities.get(entityHeadYaw.getEntityId());
            if (entity != null) {
                entity.onHeadYaw(entityHeadYaw.getHeadYaw());
            }
        } else if (packet instanceof ReplayUpdateEntityData) {
            ReplayUpdateEntityData entityData = (ReplayUpdateEntityData) packet;
            BasePacketEntity entity = this.entities.get(entityData.getEntityId());
            if (entity != null) {
                entity.onEntityMetaUpdate(entityData.getEntityData());
            }
        } else if (packet instanceof ReplayEntityEquipment) {
            ReplayEntityEquipment entityEquipment = (ReplayEntityEquipment) packet;
            BasePacketEntity entity = this.entities.get(entityEquipment.getEntityId());
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
        return entities.get(entityId);
    }

    public void remove() {
        user.sendSilent(new WrapperPlayServerDestroyEntities(this.entities.keySet().stream().mapToInt(i -> i).toArray()));
    }

    public void spawn() {
        for (ReplayPacket packet : toPacket()) {
            user.sendSilent(packet);
        }
    }

    public void clearCache() {
        entities.clear();
    }
    
    public List<ReplayPacket> toPacket() {
        return entities.values()
                .stream()
                .flatMap(entity -> entity.toPacket().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}