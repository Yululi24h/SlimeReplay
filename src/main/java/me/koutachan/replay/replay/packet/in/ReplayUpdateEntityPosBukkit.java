package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Location;

public class ReplayUpdateEntityPosBukkit extends ReplayEntityPosAndRotation {
    public ReplayUpdateEntityPosBukkit(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayUpdateEntityPosBukkit(int entityId, Location location, boolean onGround) {
        super(entityId, location, onGround);
    }
}