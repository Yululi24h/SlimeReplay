package me.koutachan.replay.packetevents;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.ReplayUserContainer;

public class PacketListener extends PacketListenerAbstract {
    @Override
    public void onUserConnect(UserConnectEvent event) {
        super.onUserConnect(event);
    }

    @Override
    public void onUserLogin(UserLoginEvent event) {
        super.onUserLogin(event);
        ReplayUserContainer.registerUser(event.getUser(), event.getPlayer());
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        super.onUserDisconnect(event);
        ReplayUserContainer.unregisterUser(event.getUser());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        super.onPacketReceive(event);
        ReplayUser user = ReplayUserContainer.getUser(event.getUser());
        if (user != null) {
            ReplayPacket packet = null;
            switch ((PacketType.Play.Client) event.getPacketType()) {
                /*case TELEPORT_CONFIRM: {
                    packet = new ReplayPacketImpl(new WrapperPlayClientTeleportConfirm(event));
                    break;
                }*/
            }
            if (user.getReplayRunner() != null) {
                user.getReplayRunner().onReceivedPacket(user, event);
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        super.onPacketSend(event);
        ReplayUser user = ReplayUserContainer.getUser(event.getUser());
        if (user != null) {
            ReplayWrapper<?> packet = null;
            switch ((PacketType.Play.Server) event.getPacketType()) {
                case RESPAWN: {
                    packet = new ReplayPlayerRespawnData(event);
                    break;
                }
                case CHUNK_DATA: {
                    packet = new ReplayChunkData(event);
                    break;
                }
                case MAP_CHUNK_BULK: {
                    packet = new ReplayChunkBulkData(event);
                    break;
                }
                case UPDATE_LIGHT: {
                    packet = new ReplayUpdateLightData(event);
                    break;
                }
                case BLOCK_CHANGE: {
                    packet = new ReplayUpdateBlock(event);
                    break;
                }
                case ENTITY_VELOCITY: {
                    packet = new ReplayEntityVelocity(event);
                    break;
                }
                case UNLOAD_CHUNK: {
                    packet = new ReplayUnloadChunkData(event);
                    break;
                }
                case SPAWN_LIVING_ENTITY:
                case SPAWN_ENTITY: {
                    packet = new ReplayLivingEntitySpawnData(event);
                    break;
                }
                case SPAWN_PAINTING: { //TODO:
                    //packet = new ReplayPacketImpl(new WrapperPlayServerSpawnPainting(event));
                    break;
                }
                case SPAWN_PLAYER: { //TODO:
                    //packet = new ReplayPacketImpl(new WrapperPlayServerSpawnPlayer(event));
                    break;
                }
                case SPAWN_EXPERIENCE_ORB: { //TODO:
                    //packet = new ReplayPacketImpl(new WrapperPlayServerSpawnExperienceOrb(event));
                    break;
                }
                case ENTITY_EQUIPMENT: {
                    packet = new ReplayEntityEquipment(event);
                    break;
                }
                case UPDATE_ENTITY_NBT: { //TODO: IDk what is that. using in 1.8
                    //packet = new ReplayPacketImpl(new WrapperPlayServerUpdateEntityNBT(event));
                    break;
                }
                case ENTITY_METADATA: {
                    packet = new ReplayUpdateEntityData(event);
                    break;
                }
                case ENTITY_RELATIVE_MOVE: {
                    packet = new ReplayEntityPos(event);
                    break;
                }
                case ENTITY_ROTATION: {
                    packet = new ReplayEntityRotation(event);
                    break;
                }
                case ENTITY_RELATIVE_MOVE_AND_ROTATION: {
                    packet = new ReplayEntityPosAndRotation(event);
                    break;
                }
                case ENTITY_TELEPORT: {
                    packet = new ReplayEntityTeleport(event);
                    break;
                }
                case ENTITY_HEAD_LOOK: {
                    packet = new ReplayEntityHeadYaw(event);
                    break;
                }
                case DESTROY_ENTITIES: {
                    packet = new ReplayDestroyEntities(event);
                    break;
                }
                case SYSTEM_CHAT_MESSAGE: { //TODO:
                    //packet = new ReplayPacketImpl(new WrapperPlayServerSystemChatMessage(event));
                    break;
                }
            }
            if (packet != null) {
                user.onPacket(packet);
            }
            if (user.getReplayRunner() != null) {
                user.getReplayRunner().onSendPacket(user, event);
            }
        }
    }
}