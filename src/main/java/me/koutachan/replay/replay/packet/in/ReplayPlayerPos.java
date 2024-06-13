package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;

public class ReplayPlayerPos extends ReplayEntityPos {
    public ReplayPlayerPos(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayPlayerPos(PacketReceiveEvent event) {
        WrapperPlayClientPlayerPosition wrapper = new WrapperPlayClientPlayerPosition(event);
        Location location = wrapper.getLocation();
        this.entityId = event.getUser().getEntityId();
        this.pos = location.getPosition();
        this.onGround = wrapper.isOnGround();
    }

    public ReplayPlayerPos(int entityId, Vector3d pos, boolean onGround) {
        this.entityId = entityId;
        this.pos = pos;
        this.onGround = onGround;
    }
}