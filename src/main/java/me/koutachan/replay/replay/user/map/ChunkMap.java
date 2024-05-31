package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkDataBulk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
import me.koutachan.replay.replay.user.PacketMap;
import me.koutachan.replay.replay.user.ReplayUser;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class ChunkMap extends PacketMap<ReplayPacket> {
    private final Map<ChunkPos, IChunkWrapper> chunks = new HashMap<>();
    private final ReplayUser user;

    public ChunkMap(ReplayUser user) {
        this.user = user;
    }

    @Override
    public void onPacket(ReplayPacket packet) {
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
                    /* TODO: WAIT WRAPPERS */
                    break;
                }
                case UNLOAD_CHUNK: {
                    WrapperPlayServerUnloadChunk chunk = (WrapperPlayServerUnloadChunk) packetWrapper;
                    chunks.remove(new ChunkPos(chunk.getChunkZ(), chunk.getChunkZ()));
                    break;
                }
            }
        }
    }

    public void clear() {
        chunks.clear();
    }

    public static class ChunkWrapper implements IChunkWrapper {
        private final ReplayPacket chunk;

        // Because of packet events is shitty.
        public ChunkWrapper(ReplayChunkData chunk) {
            this.chunk = new ReplayPacketImpl(chunk);
        }

        public ChunkWrapper(Column column) {
            this.chunk = new ReplayPacketImpl(new WrapperPlayServerChunkData(column));
        }

        @Override
        public ReplayPacket toPacket() {
            return chunk;
        }
    }

    public interface IChunkWrapper {
        ReplayPacket toPacket();
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
        List<ReplayPacket> packets = toPacket();
        packets.forEach(packet -> {
            PacketWrapper<?> wrapper = packet.toPacket();
            wrapper.resetBuffer();
            ReplayChunkData chunkData = (ReplayChunkData) wrapper;
            user.sendSilent(new WrapperPlayServerUnloadChunk(chunkData.getColumn().getX(), chunkData.getColumn().getZ()));
            user.sendSilent(wrapper);
        });

    }

    @Override
    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = chunks.values().stream()
                .map(IChunkWrapper::toPacket)
                .collect(Collectors.toList());
        Location location = user.getPlayer().getLocation();
        packets.add(new ReplayPacketImpl(new WrapperPlayServerPlayerPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), (byte) 0, 0, true)));
        return packets;
    }
}