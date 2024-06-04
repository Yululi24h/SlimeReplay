package me.koutachan.replay.replay.user.replay;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTeleportConfirm;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerChunkData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.ChunkMap;
import me.koutachan.replay.replay.user.map.data.PacketEntity;
import org.bukkit.Location;
import org.bukkit.generator.ChunkGenerator;

import java.util.*;

public class ReplayChunkHandler {
    private int viewDistance;

    private final ReplayUser user;
    private final Map<ChunkMap.ChunkPos, ReplayChunkData> chunks = new HashMap<>();

    private Location playerPos;
    private final Map<ChunkMap.ChunkPos, ReplayChunkData> loadedChunks = new HashMap<>();
    // Don't mess me...
    private final Map<Integer, PacketEntity> trackingEntity = new HashMap<>();

    private final Map<Integer, WrapperPlayServerPlayerPositionAndLook> teleportQueue = new HashMap<>();
    private int totalDelta;
    private boolean spawned;

    private ChunkMap.ChunkPos lastChunkPos;
    private int localTeleportId;

    public ReplayChunkHandler(ReplayUser user, int viewDistance) {
        this.user = user;
        this.viewDistance = viewDistance;
    }

    private static int getChunkDistance(ChunkMap.ChunkPos chunkPos, int x, int z) {
        return Math.max(Math.abs(chunkPos.getX() - x), Math.abs(chunkPos.getZ() - z));
    }

    public ChunkMap.ChunkPos toChunkPos(Location location) {
        return new ChunkMap.ChunkPos(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public ChunkMap.ChunkPos toChunkPos(ReplayChunkData chunkData) {
        return toChunkPos(chunkData.getColumn());
    }

    public ChunkMap.ChunkPos toChunkPos(Column column) {
        return new ChunkMap.ChunkPos(column.getX(), column.getZ());
    }

    public void onPosition(WrapperPlayServerPlayerPositionAndLook pos) {
        this.teleportQueue.put(pos.getTeleportId(), pos);
    }

    public void onCompleted(WrapperPlayClientTeleportConfirm teleport) {
        WrapperPlayServerPlayerPositionAndLook pos = this.teleportQueue.remove(teleport.getTeleportId());
        if (pos != null) {
            this.playerPos = new Location(null, pos.getX(), pos.getY(), pos.getZ(), pos.getYaw(), pos.getPitch());
            if (!this.spawned) {
                firstChunks();
                this.spawned = true;
            }
        }
    }

    public void firstChunks() {
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_20_4)) {
            this.user.sendSilent(new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.START_LOADING_CHUNKS, 0F));
        }
        int chunkX = floor(this.playerPos.getX()) >> 4;
        int chunkZ = floor(this.playerPos.getZ()) >> 4;
        updateChunkPos(new ChunkMap.ChunkPos(chunkX, chunkZ));
        for (int x = chunkX - this.viewDistance; x <= chunkX + this.viewDistance; ++x) {
            for (int z = chunkZ - this.viewDistance; z <= chunkZ + this.viewDistance; ++z) {
                loadChunk(new ChunkMap.ChunkPos(x, z));
            }
        }
    }

    public static int floor(double p_76128_0_) {
        int i = (int)p_76128_0_;
        return p_76128_0_ < (double)i ? i - 1 : i;
    }

    public void addDeltaMovement(WrapperPlayClientPlayerFlying pos) {
        if (pos.hasPositionChanged() && canSendChunks()) {
            this.playerPos = SpigotConversionUtil.toBukkitLocation(null, pos.getLocation());
            this.move();
        }
    }

    public boolean canSendChunks() {
        return spawned;
    }

    public void updateStatus() {
        /*for(int l = i - this.viewDistance; l <= i + this.viewDistance; ++l) {
            for(int k = j - this.viewDistance; k <= j + this.viewDistance; ++k) {
                ChunkMap.ChunkPos chunkpos = new ChunkMap.ChunkPos(l, k);
                this.updateChunkTracking(p_219234_1_, chunkpos, new IPacket[2], !p_219234_2_, p_219234_2_);
            }
        }*/
    }

    public void teleport(Location location) {
        WrapperPlayServerPlayerPositionAndLook pos = new WrapperPlayServerPlayerPositionAndLook(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), (byte) 0, localTeleportId++, true);
        this.teleportQueue.put(this.localTeleportId, pos);
        user.sendSilent(pos);
    }

    public boolean isLoadedChunk(Location location) {
        int chunkX = floor(this.playerPos.getX()) >> 4;
        int chunkZ = floor(this.playerPos.getZ()) >> 4;
        return chunks.containsKey(new ChunkMap.ChunkPos(chunkX, chunkZ));
    }


    public void setReplayTime() {

    }

    public void validReplayTimes() {

    }

    public void move() {
        if (!canSendChunks())
            return;
        //Set<ChunkMap.ChunkPos> chunkPosSet = new HashSet<>(this.loadedChunks.keySet());
        int chunkX = floor(this.playerPos.getX()) >> 4;
        int chunkZ = floor(this.playerPos.getZ()) >> 4;
        ChunkMap.ChunkPos chunkPos = new ChunkMap.ChunkPos(chunkX, chunkZ);
        if (!this.lastChunkPos.equals(chunkPos)) {
            updateChunkPos(chunkPos);
        }
        List<ChunkMap.ChunkPos> loadedPos = new ArrayList<>(loadedChunks.keySet());
        for (int x = chunkX - this.viewDistance; x <= chunkX + this.viewDistance; ++x) {
            for (int z = chunkZ - this.viewDistance; z <= chunkZ + this.viewDistance; ++z) {
                ChunkMap.ChunkPos chunkPos1 = new ChunkMap.ChunkPos(x, z);
                if (!loadedPos.remove(chunkPos1)) {
                    loadChunk(new ChunkMap.ChunkPos(x, z));
                }
            }
        }
        loadedPos.forEach(this::preUnloadChunk);
    }

    //TODO:
    public void updateViewDistance() {

    }

    public void updateChunkPos() {
        updateChunkPos(new ChunkMap.ChunkPos(floor(playerPos.getX()) >> 4, floor(playerPos.getZ()) >> 4));
    }

    public void updateChunkPos(ChunkMap.ChunkPos pos) {
        if (!Objects.equals(pos, lastChunkPos)) {
            this.lastChunkPos = pos;
            this.user.sendSilent(new WrapperPlayServerUpdateViewPosition(pos.getX(), pos.getZ()));
        }
    }

    public void loadChunk(ChunkMap.ChunkPos pos) {
        loadChunk(pos, chunks.get(pos));
    }

    public void loadChunk(ChunkMap.ChunkPos chunkPos, ReplayChunkData chunk) {
        if (chunk == null)
            return;
        ReplayChunkData loadedChunk = loadedChunks.get(chunkPos);
        if (loadedChunk != null)
            return;
        loadedChunks.put(chunkPos, chunk);
        user.sendSilent(chunk);
    }

    public void addChunk(ReplayChunkData chunk) {
        ChunkMap.ChunkPos chunkPos = toChunkPos(chunk);
        chunks.put(chunkPos, chunk);
        if (!canSendChunks() || getChunkDistance(this.lastChunkPos, chunkPos.getX(), chunkPos.getZ()) > this.viewDistance)
            return;
        loadChunk(chunkPos, chunk);
    }

    public void removeChunk(WrapperPlayServerUnloadChunk chunk) {
        ChunkMap.ChunkPos pos = new ChunkMap.ChunkPos(chunk.getChunkX(), chunk.getChunkZ());
        if (chunks.remove(pos) != null) {
            preUnloadChunk(pos);
        }
    }

    public void unloadAllChunks() {
        loadedChunks.keySet().forEach((this::preUnloadChunk));
    }


    public void preUnloadChunk(ChunkMap.ChunkPos pos) {
        ReplayChunkData chunkData = loadedChunks.remove(pos);
        if (chunkData != null) {
            System.out.println("Pre Unloading Chunk x=" + pos.getX() + " z=" + pos.getZ());
            user.sendSilent(new WrapperPlayServerUnloadChunk(pos.getX(), pos.getZ()));
        }
    }


    public static class ChunkDataCache {
        private Column column;
        private LightData lightData;
        private boolean ignoreOldData;

        /* TODO: chunk data? Idk */
        private final Map<Long, Column> columnDataAndTimes = new HashMap<>();
        private final Map<Long, LightData> lightDataAndTimes = new HashMap<>();
        private final Map<Long, WrappedBlockState> blockStatesAndTimes = new HashMap<>();

        public ChunkDataCache(WrapperPlayServerChunkData chunkData) {
            this.column = chunkData.getColumn();
            this.lightData = chunkData.getLightData();
            this.ignoreOldData = chunkData.isIgnoreOldData();
            //WrapperPlayServerBlockChange
        }

        public void setLightData(LightData lightData) {
            this.lightData = lightData;
        }

        public void updateChunkData(Column column) {
            if (column.isFullChunk()) {
                this.column = column;
            } else {
                this.column.getChunks();
            }
        }

        public void updateBlockData() {

        }

        public void toChunkData() {

        }
    }


    public void unload(int chunk) {

    }

}
