package me.koutachan.replay.replay.user.replay;

import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTeleportConfirm;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewPosition;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.koutachan.replay.replay.packet.ServerChunkData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.ChunkMap;
import me.koutachan.replay.replay.user.map.data.PacketEntity;
import org.bukkit.Location;

import java.util.*;

public class ReplayChunkHandler {
    private int viewDistance;

    private final ReplayUser user;
    private final Map<ChunkMap.ChunkPos, ServerChunkData> chunks = new HashMap<>();

    private Location playerPos;
    private final Map<ChunkMap.ChunkPos, ServerChunkData> loadedChunks = new HashMap<>();
    // Don't mess me...
    private final Map<Integer, PacketEntity> trackingEntity = new HashMap<>();

    private final Map<Integer, WrapperPlayServerPlayerPositionAndLook> teleportQueue = new HashMap<>();
    private int totalDelta;
    private boolean spawned;

    private ChunkMap.ChunkPos lastChunkPos;

    public ReplayChunkHandler(ReplayUser user, int viewDistance) {
        this.user = user;
        this.viewDistance = viewDistance;
    }

    public int getChunkDistance(ChunkMap.ChunkPos chunkPos, Location location) {
        return getChunkDistance(chunkPos, floor(location.getX() / 16.0D), floor(location.getZ() / 16.0D));
    }

    private static int getChunkDistance(ChunkMap.ChunkPos chunkPos, int x, int z) {
        int i = chunkPos.getX() - x;
        int j = chunkPos.getZ() - z;
        return Math.max(Math.abs(i), Math.abs(j));
    }

    public ChunkMap.ChunkPos toChunkPos(Location location) {
        return new ChunkMap.ChunkPos(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public ChunkMap.ChunkPos toChunkPos(ServerChunkData chunkData) {
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
        int chunkX = floor(playerPos.getX()) >> 4;
        int chunkZ = floor(playerPos.getZ()) >> 4;
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

    public void move() {
        if (!canSendChunks())
            return;
        //Set<ChunkMap.ChunkPos> chunkPosSet = new HashSet<>(this.loadedChunks.keySet());
        int chunkX = floor(this.playerPos.getX()) >> 4;
        int chunkZ = floor(this.playerPos.getZ()) >> 4;
        ChunkMap.ChunkPos chunkPos = new ChunkMap.ChunkPos(chunkX, chunkZ);
        if (this.lastChunkPos.equals(chunkPos)) {
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
        /*System.out.println("Called! ");
        boolean viewX = Math.abs(chunkX - sectionX) <= this.viewDistance * 2;
        boolean viewZ = Math.abs(chunkZ - sectionZ) <= this.viewDistance * 2;
        if (viewX && viewZ) {
            System.out.println("Ok Going! ");
            int minX = Math.min(sectionX, chunkX) - this.viewDistance;
            int minZ = Math.min(sectionZ, chunkZ) - this.viewDistance;
            int maxX = Math.max(sectionX, chunkX) + this.viewDistance;
            int maxZ = Math.max(sectionZ, chunkZ) + this.viewDistance;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    ChunkMap.ChunkPos chunkPos1 = new ChunkMap.ChunkPos(x, z);
                    boolean flagChunkPos = getChunkDistance(chunkPos1, chunkX, chunkZ) <= viewDistance;
                    boolean flagPlayerPos = getChunkDistance(chunkPos1, sectionX, sectionZ) <= viewDistance;
                    if (flagChunkPos && !flagPlayerPos) {
                        loadChunk(chunkPos1);
                    } else if (!flagChunkPos && flagPlayerPos) {
                        preUnloadChunk(chunkPos);
                    }
                }
            }
        } else {
            for (int x = chunkPos.getX() - this.viewDistance; x <= chunkPos.getX() + this.viewDistance; x++) {
                for (int z = chunkPos.getZ() - this.viewDistance; z <= chunkPos.getZ() + this.viewDistance; z++) {
                    loadChunk(new ChunkMap.ChunkPos(x, z));
                }
            }
            for (int x = sectionX - this.viewDistance; x <= sectionX + this.viewDistance; x++) {
                for (int z = sectionZ - this.viewDistance; z <= sectionZ + this.viewDistance; z++) {
                    preUnloadChunk(chunkPos);
                }
            }
        }*/
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
            user.sendSilent(new WrapperPlayServerUpdateViewPosition(pos.getX(), pos.getZ()));
        }
    }

    public void loadChunk(ChunkMap.ChunkPos pos) {
        loadChunk(pos, chunks.get(pos));
    }

    public void loadChunk(ChunkMap.ChunkPos chunkPos, ServerChunkData chunk) {
        if (chunk == null)
            return;
        ServerChunkData loadedChunk = loadedChunks.get(chunkPos);
        if (loadedChunk != null)
            return;
        loadedChunks.put(chunkPos, chunk);
        user.sendSilent(chunk);
    }

    public void addChunk(ServerChunkData chunk) {
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
        ServerChunkData chunkData = loadedChunks.remove(pos);
        if (chunkData != null) {
            System.out.println("Pre Unloading Chunk x=" + pos.getX() + " z=" + pos.getZ());
            user.sendSilent(new WrapperPlayServerUnloadChunk(pos.getX(), pos.getZ()));
        }
    }


    public static class WrapperChunkData {
        private Column column;
        private WrapperChunkData data;

        public WrapperChunkData(WrapperChunkData data) {

        }

    }


    public void unload(int chunk) {

    }

}
