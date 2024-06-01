package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;
import java.util.List;

public class ReplayStartPacket extends ReplayWrapper<ReplayStartPacket> {
    private List<ReplayChunkData> chunkData;
    private List<EntitySpawnData> entitySpawn;
    private PlayerSelfData playerSelf;
    private Dimension dimension;
    private Location startPos;

    private int height;

    public ReplayStartPacket(ReplayUser user) {
        super();

    }

    @Override
    public void read() {
        int chunkSize = readInt();
        for (int i = 0; i < chunkSize; i++) {
            //ReplayChunkData chunkData = new ReplayChunkData(this.serverVersion, this.buffer);
        }
        int entitySize = readInt();
        this.entitySpawn = new ArrayList<>();
        for (int i = 0; i < entitySize; i++) {
            entitySpawn.add(new EntitySpawnData(this.serverVersion, this.buffer));
        }

        this.playerSelf = new PlayerSelfData();
        this.dimension = readDimension();
    }

    @Override
    public void write() {
        super.write();
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
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
