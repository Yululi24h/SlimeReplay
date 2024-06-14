package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateViewPosition;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.ChunkCache;
import me.koutachan.replay.replay.user.map.data.PacketEntity;
import me.koutachan.replay.replay.user.map.data.PacketEntitySelf;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ReplayRunnerHandler {
    private ReplayUser user;

    private final Set<ChunkCache.ChunkPos> sentChunks = new HashSet<>();
    private final List<ReplayChunkData> currentChunks = new ArrayList<>();
    private final List<PacketEntity> sentEntities = new ArrayList<>();
    private ReplayChain current;

    private Location playerPos;
    private ChunkCache.ChunkPos lastChunkPos;

    private PacketEntitySelf self;
    private Dimension dimension;

    private int chunkRadius;

    public ReplayRunnerHandler(ReplayUser user) {
        this.user = user;
        this.user.getEntityId();
    }

    @Nullable
    public PacketEntity getEntity(int entityId) {
        return this.sentEntities.stream()
                .filter(entity -> entity.getEntityId() == entityId)
                .findFirst()
                .orElse(null);
    }

    public void handleChunkRadius(int chunkRadius) {
        this.chunkRadius = chunkRadius;
    }

    public void handleChunk(ReplayChunkData data) {
        this.currentChunks.add(data);
    }

    public boolean hasChunk(ReplayChunkData chunkData) {
        return hasChunk(chunkData.getX(), chunkData.getZ());
    }

    public boolean hasChunk(int x, int z) {
        return this.currentChunks.stream().anyMatch(chunk -> chunk.getX() == x && chunk.getZ() == z);
    }

    public ReplayChunkData getChunk(ChunkCache.ChunkPos pos) {
        return this.currentChunks.stream()
                .filter(chunk -> chunk.getX() == pos.getX() && chunk.getZ() == pos.getZ())
                .findFirst()
                .orElse(null);
    }

    public boolean canSendChunks() {
        return true;
    }

    public void updateChunkPos(ChunkCache.ChunkPos pos) {
        if (!Objects.equals(pos, lastChunkPos)) {
            this.lastChunkPos = pos;
            this.user.sendSilent(new WrapperPlayServerUpdateViewPosition(pos.getX(), pos.getZ()));
        }
    }

    public void loadChunk(ChunkCache.ChunkPos pos) {
        ReplayChunkData chunkData = getChunk(pos);
        if (chunkData != null) {
            this.user.sendSilent(chunkData.getPackets());
            this.sentChunks.add(pos);
        }
    }

    public void unloadChunk(ChunkCache.ChunkPos pos) {
        if (this.sentChunks.remove(pos)) {
            this.user.sendSilent(new WrapperPlayServerUnloadChunk(pos.getX(), pos.getZ()));
        }
    }

    public void move() {
        if (!canSendChunks())
            return;
        //Set<ChunkMap.ChunkPos> chunkPosSet = new HashSet<>(this.loadedChunks.keySet());
        int chunkX = floor(this.playerPos.getX()) >> 4;
        int chunkZ = floor(this.playerPos.getZ()) >> 4;
        ChunkCache.ChunkPos chunkPos = new ChunkCache.ChunkPos(chunkX, chunkZ);
        if (!this.lastChunkPos.equals(chunkPos)) {
            updateChunkPos(chunkPos);
        }
        List<ChunkCache.ChunkPos> loadedPos = new ArrayList<>(sentChunks);
        for (int x = chunkX - this.chunkRadius; x <= chunkX + this.chunkRadius; ++x) {
            for (int z = chunkZ - this.chunkRadius; z <= chunkZ + this.chunkRadius; ++z) {
                ChunkCache.ChunkPos chunkPos1 = new ChunkCache.ChunkPos(x, z);
                if (!loadedPos.remove(chunkPos1)) {
                    loadChunk(new ChunkCache.ChunkPos(x, z));
                }
            }
        }
        loadedPos.forEach(this::unloadChunk);
    }

    public static int floor(double p_76128_0_) {
        int i = (int)p_76128_0_;
        return p_76128_0_ < (double)i ? i - 1 : i;
    }

    public void onSpawn(Dimension dimension, GameMode gameMode) {
        this.user.sendSilent(new WrapperPlayServerRespawn(
                dimension,
                "Slime-Replay-InDev",
                Difficulty.NORMAL, //TODO: I don't think we need to set up a difficulty here,
                0L,
                gameMode,
                gameMode,
                false,
                false,
                (byte) 0,
                null,
                null
        ));
    }

    public void onMove() {

    }

    public void sentEntity() {

    }

    public void nextChain() {

    }

    public void backChain(int millis) {
        ReplayChain current = this.current;
        List<ReplayChain> collectedChain = new ArrayList<>(); //TODO:
        while (millis > current.getMillis()) {
            collectedChain.add(current);
            current = current.back();
        }
        this.current = current;
    }

    private static int getChunkDistance(ChunkCache.ChunkPos chunkPos, int x, int z) {
        return Math.max(Math.abs(chunkPos.getX() - x), Math.abs(chunkPos.getZ() - z));
    }
}