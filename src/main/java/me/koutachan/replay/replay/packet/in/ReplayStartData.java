package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.ArrayList;
import java.util.List;

public class ReplayStartData extends ReplayWrapper<ReplayStartData> {
    private List<ReplayChunkData> chunkData;
    private List<ReplayLivingEntitySpawnData> entityData;

    private Dimension dimension;
    /*private ReplayPlayerSelfData playerSelf;

    private int height;*/

    public ReplayStartData(ReplayUser user) {
    }

    public ReplayStartData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayStartData(List<ReplayChunkData> chunkData, List<ReplayLivingEntitySpawnData> entityData, Dimension dimension) {
        this.chunkData = chunkData;
        this.entityData = entityData;
        this.dimension = dimension;
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
        //this.playerSelf = new ReplayPlayerSelfData(this.serverVersion, this.buffer);
        this.dimension = readDimension();
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
        writeDimension(this.dimension);
    }

    public List<ReplayChunkData> getChunkData() {
        return chunkData;
    }

    public List<ReplayLivingEntitySpawnData> getEntityData() {
        return entityData;
    }

    public Dimension getDimension() {
        return dimension;
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