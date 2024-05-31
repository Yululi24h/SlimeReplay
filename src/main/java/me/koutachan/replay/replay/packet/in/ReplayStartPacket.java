package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.List;

public class ReplayStartPacket extends ReplayWrapper<ReplayStartPacket> {
    private List<ReplayChunkData> chunkData;
    private List<EntitySpawnData> entitySpawn;
    private Dimension dimension;
    private Location startPos;

    private int height;

    public ReplayStartPacket(ReplayUser user) {
        super();
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
