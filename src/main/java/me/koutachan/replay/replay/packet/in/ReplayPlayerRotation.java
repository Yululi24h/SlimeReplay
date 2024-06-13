package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;

public class ReplayPlayerRotation extends ReplayEntityRotation {
    public ReplayPlayerRotation(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayPlayerRotation(PacketReceiveEvent event) {
        WrapperPlayClientPlayerRotation wrapper = new WrapperPlayClientPlayerRotation(event);
        Location location = wrapper.getLocation();
        this.entityId = event.getUser().getEntityId();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.onGround = wrapper.isOnGround();
    }

    public ReplayPlayerRotation(int entityId, float yaw, float pitch, boolean onGround) {
        this.entityId = entityId;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }
}