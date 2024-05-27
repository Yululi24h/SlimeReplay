package me.koutachan.replay.packetevents;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
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

            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        super.onPacketSend(event);
        ReplayUser user = ReplayUserContainer.getUser(event.getUser());
        if (user != null) {
            ReplayPacket packet = null;
            switch ((PacketType.Play.Server) event.getPacketType()) {
                case JOIN_GAME: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerJoinGame(event));
                    break;
                }
                case RESPAWN: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerRespawn(event));
                    break;
                }
                case CHUNK_DATA: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerChunkData(event));
                    break;
                }
                case MAP_CHUNK_BULK: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerChunkDataBulk(event));
                    break;
                }
                case ENTITY_VELOCITY: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityVelocity(event));
                    break;
                }
                case UNLOAD_CHUNK: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerUnloadChunk(event));
                    break;
                }
                case SPAWN_ENTITY: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerSpawnEntity(event));
                    break;
                }
                case SPAWN_LIVING_ENTITY: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerSpawnLivingEntity(event));
                    break;
                }
                case SPAWN_PAINTING: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerSpawnPainting(event));
                    break;
                }
                case SPAWN_PLAYER: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerSpawnPlayer(event));
                    break;
                }
                case SPAWN_EXPERIENCE_ORB: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerSpawnExperienceOrb(event));
                    break;
                }
                case ENTITY_EQUIPMENT: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityEquipment(event));
                    break;
                }
                case UPDATE_ENTITY_NBT: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerUpdateEntityNBT(event));
                    break;
                }
                case ENTITY_METADATA: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityMetadata(event));
                    break;
                }
                case ENTITY_RELATIVE_MOVE: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityRelativeMove(event));
                    break;
                }
                case ENTITY_ROTATION: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityRotation(event));
                    break;
                }
                case ENTITY_RELATIVE_MOVE_AND_ROTATION: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityRelativeMoveAndRotation(event));
                    break;
                }
                case ENTITY_TELEPORT: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityTeleport(event));
                    break;
                }
                case ENTITY_HEAD_LOOK: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerEntityHeadLook(event));
                    break;
                }
                case DESTROY_ENTITIES: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerDestroyEntities(event));
                    break;
                }
            }
            if (packet != null) {
                user.onPacket(packet);
            }
        }
    }
}