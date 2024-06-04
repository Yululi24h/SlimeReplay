package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReplayChunkBulkData extends ReplayWrapper<ReplayChunkBulkData> {
    private int[] x;
    private int[] z;
    private BaseChunk[][] baseChunks;
    private byte[] biomeData;

    public ReplayChunkBulkData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        if (CURRENT_VERSION.isNewerThanOrEquals(ServerVersion.V_1_9)) {
            LightData data;
            for (Column column : toColumn()) {
                //column
                //packets.add(new WrapperPlayServerChunkData(column, new LightData()));
            }
        } else {

        }

        return null;
    }

    public Column[] toColumn() {
        Column[] columns = new Column[this.baseChunks.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Column(
                    this.x[i],
                    this.z[i],
                    true,
                    this.baseChunks[i],
                    new TileEntity[0],
                    fill(this.biomeData[i], 1024)
            );
        }
        return columns;
    }

    public byte[] fill(byte b, int length) {
        byte[] array = new byte[length];
        Arrays.fill(array, b);
        return array;

    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
