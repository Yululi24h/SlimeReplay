package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.user.PacketMap;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.data.*;

import java.util.*;
import java.util.stream.Collectors;

public class EntitiesData extends PacketMap<ReplayPacket> {
    private final Map<Integer, BasePacketEntity> entities = new HashMap<>();
    private final ReplayUser user;

    public EntitiesData(ReplayUser user) {
        this.user = user;
    }

    @Override
    public void onPacket(ReplayPacket packet) {
        PacketWrapper<?> packetWrapper = packet.toPacket();
        PacketTypeCommon packetType = packetWrapper.getPacketTypeData().getPacketType();
        PacketSide side = packetType.getSide();
        if (side == PacketSide.SERVER) {
            switch ((PacketType.Play.Server) packetType) {
                case SPAWN_ENTITY: {
                    WrapperPlayServerSpawnEntity entity = (WrapperPlayServerSpawnEntity) packetWrapper;
                    entities.put(entity.getEntityId(), new PacketEntity(user, entity));
                    break;
                }
                case SPAWN_LIVING_ENTITY: {
                    WrapperPlayServerSpawnLivingEntity entity = (WrapperPlayServerSpawnLivingEntity) packetWrapper;
                    entities.put(entity.getEntityId(), new PacketLivingEntity(user, entity));
                    break;
                }
                case SPAWN_EXPERIENCE_ORB: {
                    WrapperPlayServerSpawnExperienceOrb entity = (WrapperPlayServerSpawnExperienceOrb) packetWrapper;
                    entities.put(entity.getEntityId(), new PacketExperienceEntity(user, entity));
                    break;
                }
                case SPAWN_PAINTING: {
                    WrapperPlayServerSpawnPainting entity = (WrapperPlayServerSpawnPainting) packetWrapper;
                    entities.put(entity.getEntityId(), new PacketPaintingEntity(user, entity));
                    break;
                }
                case ENTITY_RELATIVE_MOVE: {
                    WrapperPlayServerEntityRelativeMove move = (WrapperPlayServerEntityRelativeMove) packetWrapper;
                    BasePacketEntity entity = this.entities.get(move.getEntityId());
                    if (entity != null) {
                        entity.onMove(move.getDeltaX(), move.getDeltaY(), move.getDeltaZ());
                    }
                    break;
                }
                case ENTITY_VELOCITY: {
                    WrapperPlayServerEntityVelocity velocity = (WrapperPlayServerEntityVelocity) packetWrapper;
                    BasePacketEntity entity = this.entities.get(velocity.getEntityId());
                    if (entity != null) {
                        entity.onVelocity();
                    }
                    break;
                }
                case ENTITY_ROTATION: {
                    WrapperPlayServerEntityRotation rotation = (WrapperPlayServerEntityRotation) packetWrapper;
                    BasePacketEntity entity = this.entities.get(rotation.getEntityId());
                    if (entity != null) {
                        entity.onRotation(rotation.getYaw(), rotation.getPitch());
                    }
                    break;
                }
                case ENTITY_RELATIVE_MOVE_AND_ROTATION: {
                    WrapperPlayServerEntityRelativeMoveAndRotation moveAndRotation = (WrapperPlayServerEntityRelativeMoveAndRotation) packetWrapper;
                    BasePacketEntity entity = this.entities.get(moveAndRotation.getEntityId());
                    if (entity != null) {
                        entity.onMove(moveAndRotation.getDeltaX(), moveAndRotation.getDeltaY(), moveAndRotation.getDeltaZ());
                        entity.onRotation(moveAndRotation.getYaw(), moveAndRotation.getPitch());
                    }
                    break;
                }
                case ENTITY_TELEPORT: {
                    WrapperPlayServerEntityTeleport teleport = (WrapperPlayServerEntityTeleport) packetWrapper;
                    BasePacketEntity entity = this.entities.get(teleport.getEntityId());
                    if (entity != null) {
                        Vector3d position = teleport.getPosition();
                        entity.onPosition(new Location(position.getX(), position.getY(), position.getZ(), teleport.getYaw(), teleport.getPitch()));
                    }
                    break;
                }
                case ENTITY_HEAD_LOOK: {
                    WrapperPlayServerEntityHeadLook headLook = (WrapperPlayServerEntityHeadLook) packetWrapper;
                    BasePacketEntity entity = this.entities.get(headLook.getEntityId());
                    if (entity != null) {
                        entity.onHeadYaw(headLook.getHeadYaw());
                    }
                    break;
                }
                case ENTITY_METADATA: {
                    WrapperPlayServerEntityMetadata metadata = (WrapperPlayServerEntityMetadata) packetWrapper;
                    BasePacketEntity entity = this.entities.get(metadata.getEntityId());
                    if (entity != null) {
                        entity.onEntityMetaUpdate(metadata.getEntityMetadata());
                    }
                    break;
                }
                case ENTITY_EQUIPMENT: {
                    WrapperPlayServerEntityEquipment equipment = (WrapperPlayServerEntityEquipment) packetWrapper;
                    BasePacketEntity entity = this.entities.get(equipment.getEntityId());
                    if (entity != null) {
                        entity.onEquipmentUpdate(equipment.getEquipment());
                    }
                    break;
                }
                /*case UPDATE_ENTITY_NBT: {
                    WrapperPlayServerUpdateEntityNBT nbt = (WrapperPlayServerUpdateEntityNBT) packetWrapper;
                    BasePacketEntity entity = this.entities.get(nbt.getEntityId());
                    if (entity != null) {
                        entity.onNBTUpdate(nbt.getNBTCompound());
                    }
                    break;
                }*/
                case DESTROY_ENTITIES: {
                    WrapperPlayServerDestroyEntities destroyEntities = (WrapperPlayServerDestroyEntities) packetWrapper;
                    for (int entityId : destroyEntities.getEntityIds()) {
                        entities.remove(entityId);
                    }
                    break;
                }

                /*case RESPAWN: {
                    WrapperPlayServerRespawn respawn = (WrapperPlayServerRespawn) packetWrapper;
                    entities.clear();
                    break;
                }*/
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

    public void clear() {
        entities.clear();
    }

    @Override
    public List<ReplayPacket> toPacket() {
        return entities.values()
                .stream()
                .flatMap(entity -> entity.toPacket().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}