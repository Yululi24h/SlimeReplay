package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;

public class ReplayPlayerPosAndRotation extends ReplayEntityPosAndRotation {
    public ReplayPlayerPosAndRotation(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayPlayerPosAndRotation(PacketReceiveEvent event) {
        WrapperPlayClientPlayerPositionAndRotation wrapper = new WrapperPlayClientPlayerPositionAndRotation(event);
        this.entityId = event.getUser().getEntityId();
        this.location = wrapper.getLocation();
        this.onGround = wrapper.isOnGround();
    }

    public ReplayPlayerPosAndRotation(int entityId, Location location, boolean onGround) {
        this.entityId = entityId;
        this.location = location;
        this.onGround = onGround;
    }
}