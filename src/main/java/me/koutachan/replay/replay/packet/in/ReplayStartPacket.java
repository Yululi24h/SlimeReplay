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
    private List<EntitySpawnData> entityData;
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
        this.entityData = new ArrayList<>();
        for (int i = 0; i < entitySize; i++) {
            this.entityData.add(new EntitySpawnData(this.serverVersion, this.buffer));
        }
        this.playerSelf = new PlayerSelfData(this.serverVersion, this.buffer);
        this.dimension = readDimension();
    }

    @Override
    public void write() {
        writeInt(this.chunkData.size());
        for (ReplayChunkData chunkData : this.chunkData) {
            writeWrapper(chunkData);
        }
        writeInt(this.entityData.size());
        for (EntitySpawnData entityData : this.entityData) {
            writeWrapper(entityData);
        }
        writeWrapper(this.playerSelf);
        writeDimension(this.dimension);

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
