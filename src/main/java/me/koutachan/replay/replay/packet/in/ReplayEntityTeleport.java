package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;

import java.util.ArrayList;
import java.util.List;

public class ReplayEntityTeleport extends ReplayEntityPosAndRotation {
    public ReplayEntityTeleport(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayEntityTeleport(PacketSendEvent event) {
        WrapperPlayServerEntityTeleport wrapper = new WrapperPlayServerEntityTeleport(event);
        this.entityId = wrapper.getEntityId();
        this.location = new Location(wrapper.getPosition(), wrapper.getYaw(), wrapper.getPitch());
        this.onGround = wrapper.isOnGround();
    }

    public ReplayEntityTeleport(int entityId, Location location, boolean onGround) {
        super(entityId, location, onGround);
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerEntityTeleport(
                this.entityId,
                this.location,
                this.onGround
        ));
        return packets;
    }
}
