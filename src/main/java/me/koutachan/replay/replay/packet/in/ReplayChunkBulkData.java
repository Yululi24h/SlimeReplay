package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerChunkDataBulk;

import java.util.ArrayList;
import java.util.List;

public class ReplayChunkBulkData extends ReplayWrapper<ReplayChunkBulkData> {
    private WrapperPlayServerChunkDataBulk chunkData;

    public ReplayChunkBulkData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayChunkBulkData(PacketSendEvent event) {
        this.chunkData = new WrapperPlayServerChunkDataBulk(event);
    }

    @Override
    public void read() {
        this.chunkData = readWrapper(new WrapperPlayServerChunkDataBulk());
    }

    @Override
    public void write() {
        writeWrapper(this.chunkData);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(this.chunkData);
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        int length = this.chunkData.getChunks().length;
        int[] x = this.chunkData.getX();
        int[] z = this.chunkData.getZ();
        for (int i = 0; i < length; i++) {
            packets.add(new WrapperPlayServerUnloadChunk(
                    x[i],
                    z[i]
            ));
        }
        return packets;
    }
}
