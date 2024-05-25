package me.koutachan.replay.packetevents;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.impl.ReplayPacketImpl;
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
                case CHUNK_DATA: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerChunkData(event));
                    break;
                }
                case MAP_CHUNK_BULK: {
                    packet = new ReplayPacketImpl(new WrapperPlayServerChunkDataBulk(event));
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
            }
            if (packet != null) {
                user.onPacket(packet);
            }
        }
    }
}