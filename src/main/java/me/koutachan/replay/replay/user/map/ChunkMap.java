package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.ShortArray3d;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.GlobalPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.ListPalette;
import com.github.retrooper.packetevents.protocol.world.chunk.palette.PaletteType;
import com.github.retrooper.packetevents.protocol.world.chunk.storage.LegacyFlexibleStorage;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.packet.in.*;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.user.PacketMap;
import me.koutachan.replay.replay.user.ReplayUser;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChunkMap extends PacketMap<ReplayPacket> {
    private final Map<ChunkPos, IChunkWrapper> chunks = new HashMap<>();
    private final ReplayUser user;

    public ChunkMap(ReplayUser user) {
        this.user = user;
    }

    @Override
    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayChunkData) {
            ReplayChunkData chunkData = (ReplayChunkData) packet;
            this.chunks.put(new ChunkPos(chunkData.getX(), chunkData.getZ()), new ChunkWrapper(chunkData));
            return;
        }
        if (packet instanceof ReplayChunkBulkData) {
            ReplayChunkBulkData chunkDataBulk = (ReplayChunkBulkData) packet;
            for (ReplayChunkData chunkData : chunkDataBulk.getChunks()) {
                this.chunks.put(new ChunkPos(chunkData.getX(), chunkData.getZ()), new ChunkWrapper(chunkData));
            }
            return;
        }
        if (packet instanceof ReplayUpdateLightData) {
            ReplayUpdateLightData lightData = (ReplayUpdateLightData) packet;
            IChunkWrapper wrapper = this.chunks.get(new ChunkPos(lightData.getX(), lightData.getZ()));
            if (wrapper != null) {
                wrapper.setLightData(lightData.getLightData());
            }
            return;
        }
        if (packet instanceof ReplayUpdateBlock) {
            ReplayUpdateBlock block = (ReplayUpdateBlock) packet;
            IChunkWrapper wrapper = this.chunks.get(block.getChunkPos());
            if (wrapper != null) {
                wrapper.setBlock(block.getBlockPos(), block.getBlockId());
            }
            return;
        }
        if (packet instanceof ReplayUnloadChunkData) {
            ReplayUnloadChunkData chunkData = (ReplayUnloadChunkData) packet;
            this.chunks.remove(new ChunkPos(chunkData.getX(), chunkData.getZ()));
            return;
        }
    }

    @Override
    public void onPacket(ReplayPacket packet) {
        /*
        PacketWrapper<?> packetWrapper = packet.toPacket();
        PacketTypeCommon packetType = packetWrapper.getPacketTypeData().getPacketType();
        PacketSide side = packetType.getSide();
        if (side == PacketSide.SERVER) {
            switch ((PacketType.Play.Server) packetType) {
                case CHUNK_DATA: {
                    ReplayChunkData chunk = (ReplayChunkData) packetWrapper;
                    chunks.put(new ChunkPos(chunk.getColumn().getX(), chunk.getColumn().getZ()), new ChunkWrapper(chunk));
                    break;
                }
                case MAP_CHUNK_BULK: {
                    WrapperPlayServerChunkDataBulk chunk = (WrapperPlayServerChunkDataBulk) packetWrapper;
                    for (int i = 0; i < chunk.getChunks().length; i++) {
                        int x = chunk.getX()[i];
                        int z = chunk.getZ()[i];
                        BaseChunk[] baseChunk = chunk.getChunks()[i];
                        //TODO: Fixes //TODO: TO INT[]
                        byte[] biomeData = chunk.getBiomeData()[i];
                        Column column = new Column(x, z, true, baseChunk, new TileEntity[0]);
                        chunks.put(new ChunkPos(x, z), new ChunkWrapper(column));
                    }
                    break;
                }
                case CHUNK_BIOMES: {
                    break;
                }
                case UNLOAD_CHUNK: {
                    WrapperPlayServerUnloadChunk chunk = (WrapperPlayServerUnloadChunk) packetWrapper;
                    chunks.remove(new ChunkPos(chunk.getChunkZ(), chunk.getChunkZ()));
                    break;
                }
            }
        }*/
    }

    public void clear() {
        chunks.clear();
    }

    public static class ChunkWrapper implements IChunkWrapper {
        private final ReplayChunkData chunk;

        // Because of packet events is shitty.
        public ChunkWrapper(ReplayChunkData chunk) {
            this.chunk = chunk;
        }

        public ChunkWrapper(Column column) {
            this.chunk = null;
        }

        @Override
        public Column getColumn() {
            return chunk.getColumn();
        }

        @Override
        public void setLightData(LightData lightData) {
            chunk.setLightData(lightData);
        }

        @Override
        public void setBlock(Vector3i pos, int blockId) {
            BaseChunk[] baseChunks = chunk.getColumn().getChunks();
            int y = pos.getY() >> 4;
            if (baseChunks.length <= y || y < 0)
                return;
            BaseChunk baseChunk = baseChunks[y];
            if (baseChunk == null) {
                baseChunk = BaseChunk.create();
                //baseChunk.set(0, 0, 0, 0);
            }
            baseChunk.set(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, blockId);
        }

        @Override
        public ReplayChunkData toPacket() {
            return chunk;
        }
    }

    public interface IChunkWrapper {
        Column getColumn();

        void setLightData(LightData lightData);

        void setBlock(Vector3i pos, int blockId);

        ReplayChunkData toPacket();
    }

    public static class ChunkPos {
        private final int x, z;

        public ChunkPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChunkPos)) return false;
            ChunkPos chunkPos = (ChunkPos) o;
            return x == chunkPos.x && z == chunkPos.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
    }

    public void sentPacket(ReplayUser user) {

    }

    @Override
    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = chunks.values().stream()
                .map(IChunkWrapper::toPacket)
                .collect(Collectors.toList());
        Location location = user.getPlayer().getLocation();
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerPlayerPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), (byte) 0, 0, true)));
        return packets;
    }
}