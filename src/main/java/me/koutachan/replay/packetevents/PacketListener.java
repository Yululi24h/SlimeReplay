package me.koutachan.replay.packetevents;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.ReplayUserContainer;

public class PacketListener extends PacketListenerAbstract {
    @Override
    public void onUserLogin(UserLoginEvent event) {
        ReplayUserContainer.registerUser(event.getUser(), event.getPlayer());
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        ReplayUserContainer.unregisterUser(event.getUser());
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        ReplayUser user = ReplayUserContainer.getUser(event.getUser());
        if (user != null) {
            ReplayWrapper<?> packet = null;
            switch ((PacketType.Play.Client) event.getPacketType()) {
                case PLAYER_POSITION: {
                    packet = new ReplayPlayerPos(event);
                    break;
                }
                case PLAYER_ROTATION: {
                    packet = new ReplayPlayerRotation(event);
                    break;
                }
                case PLAYER_POSITION_AND_ROTATION: {
                    packet = new ReplayPlayerPosAndRotation(event);
                    break;
                }
            }
           if (packet != null) {
               user.onPacket(packet);
           }
           if (user.isReplaying()) {
               user.getReplayRunner().onReceivedPacket(user, event);
           }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        super.onPacketSend(event);
        if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event);
            System.out.println("Received Player Info!");
            System.out.println(info.getAction());
        }

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
                    //packet = new ReplayLivingEntitySpawnData(event); / TODO:
                    break;
                }
                case SPAWN_PAINTING: { //TODO:
                    //packet = new ReplayPacketImpl(new WrapperPlayServerSpawnPainting(event));
                    break;
                }
                case SPAWN_PLAYER: { //TODO:
                    packet = new ReplaySpawnPlayer(event);
                    break;
                }
                case SPAWN_EXPERIENCE_ORB: { //TODO:
                    //packet = new ReplayPacketImpl(new WrapperPlayServerSpawnExperienceOrb(event));
                    break;
                }
                case PLAYER_INFO: {
                    packet = ReplayPlayerInfo.fromPacketEvent(new WrapperPlayServerPlayerInfo(event));
                    break;
                }
                case ENTITY_EQUIPMENT: {
                    packet = new ReplayEntityEquipment(event);
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
            if (user.isReplaying()) {
                user.getReplayRunner().onSendPacket(user, event);
            }
        }
    }
}