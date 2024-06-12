package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;
import java.util.List;

public class ReplayStartData extends ReplayWrapper<ReplayStartData> {
    private List<ReplayChunkData> chunkData;
    private List<ReplayLivingEntitySpawnData> entityData;
    private ReplayPlayerSelfData playerSelf;

    private int height;

    public ReplayStartData(ReplayUser user) {
    }

    public ReplayStartData(List<ReplayChunkData> chunkData, List<ReplayLivingEntitySpawnData> entityData, ReplayPlayerSelfData playerSelf) {
        this.chunkData = chunkData;
        this.entityData = entityData;
        this.playerSelf = playerSelf;
    }

    @Override
    public void read() {
        int chunkSize = readVarInt();
        for (int i = 0; i < chunkSize; i++) {
            this.chunkData.add(new ReplayChunkData(this.serverVersion, this.buffer));
        }
        int entitySize = readVarInt();
        this.entityData = new ArrayList<>();
        for (int i = 0; i < entitySize; i++) {
            this.entityData.add(new ReplayLivingEntitySpawnData(this.serverVersion, this.buffer));
        }
        this.playerSelf = new ReplayPlayerSelfData(this.serverVersion, this.buffer);
        //this.dimension = readDimension();
    }

    @Override
    public void write() {
        writeVarInt(this.chunkData.size());
        for (ReplayChunkData chunkData : this.chunkData) {
            writeWrapper(chunkData);
        }
        writeVarInt(this.entityData.size());
        for (ReplayLivingEntitySpawnData entityData : this.entityData) {
            writeWrapper(entityData);
        }
        writeWrapper(this.playerSelf);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}
