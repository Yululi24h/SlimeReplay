package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateBlock;
import me.koutachan.replay.replay.packet.in.ReplayUpdateMultipleBlock;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerUpdateLight;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.ChunkCache;
import me.koutachan.replay.replay.user.map.data.PacketEntity;
import me.koutachan.replay.replay.user.replay.chain.impl.ReplayStartDataChain;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplayRunnerHandler {
    private final ReplayUser user;

    private final Set<ChunkCache.ChunkPos> sentChunks = new HashSet<>();
    private final List<ReplayChunkData> currentChunks = new ArrayList<>();
    private final List<PacketEntity> currentEntities = new ArrayList<>();
    private final List<PacketEntity> sentEntities = new ArrayList<>();
    private ReplayChain current;

    private Location playerPos;
    private ChunkCache.ChunkPos lastChunkPos;
    private Dimension dimension;
    private int chunkRadius;
    private final List<TeleportQueue> teleportQueues = new ArrayList<>();
    private int localId; //Uses in teleport

    private long millis;

    public ReplayRunnerHandler(ReplayUser user, ReplayChain current) {
        this.user = user;
        this.user.getEntityId();
        this.current = current;
        if (!(current instanceof ReplayStartDataChain)) { //Determined ReplayStartDataChain
            throw new IllegalStateException();
        }
        this.chunkRadius = 8;
        this.current.send(this);
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
        if (canSendChunks() && this.lastChunkPos != null && this.chunkRadius >= getChunkDistance(this.lastChunkPos, data.getX(), data.getZ()) ) {
            loadChunk(data.toChunkPos());
        }
    }

    public boolean hasChunk(ReplayChunkData chunkData) {
        return hasChunk(chunkData.getX(), chunkData.getZ());
    }

    public boolean hasChunk(int x, int z) {
        return this.currentChunks.stream().anyMatch(chunk -> chunk.getX() == x && chunk.getZ() == z);
    }

    public boolean hasSentChunk(int x, int z) {
        return this.sentChunks.stream().anyMatch(chunk -> chunk.getX() == x && chunk.getZ() == z);
    }

    public ReplayChunkData getChunk(ChunkCache.ChunkPos pos) {
        return this.currentChunks.stream()
                .filter(chunk -> chunk.getX() == pos.getX() && chunk.getZ() == pos.getZ())
                .findFirst()
                .orElse(null);
    }

    public void onMove(Location location) {
        if (this.playerPos == null || !playerPos.getPosition().equals(location.getPosition())) {
            this.playerPos = location;
            this.move();
        }
    }

    public void move() {
        if (!canSendChunks())
            return;
        int chunkX = floor(this.playerPos.getX()) >> 4;
        int chunkZ = floor(this.playerPos.getZ()) >> 4;
        ChunkCache.ChunkPos chunkPos = new ChunkCache.ChunkPos(chunkX, chunkZ);
        if (this.lastChunkPos == null || !this.lastChunkPos.equals(chunkPos)) {
            updateChunkPos(chunkPos);
        }
        List<ChunkCache.ChunkPos> loadedPos = new ArrayList<>(this.sentChunks);
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

    public void loadChunk(ChunkCache.ChunkPos pos) {
        ReplayChunkData chunkData = getChunk(pos);
        if (chunkData != null) {
            this.user.sendSilent(chunkData.getPackets());
            if (chunkData.getLightData() != null && chunkData.getServerVersion().isOlderThan(ServerVersion.V_1_18)) {
                this.user.sendSilent(new WrapperPlayServerUpdateLight(chunkData.getX(), chunkData.getZ(), chunkData.getLightData()));
            }
            this.sentChunks.add(pos);
        }
    }

    public void unloadChunk(ChunkCache.ChunkPos pos) {
        if (this.sentChunks.remove(pos)) {
            this.user.sendSilent(new WrapperPlayServerUnloadChunk(pos.getX(), pos.getZ()));
        }
    }

    public void onCompleteTeleport(int teleportId) {
        TeleportQueue teleportQueue = this.teleportQueues.stream()
                .filter(queue -> queue.teleportId == teleportId)
                .findFirst()
                .orElse(null);
        if (teleportQueue == null)
            return;
        this.teleportQueues.remove(teleportQueue);
        if (canSendChunks()) {
            onMove(teleportQueue.getPlayerPos());
        }
    }

    public boolean canSendChunks() {
        return this.teleportQueues.isEmpty();
    }

    public void updateChunkPos(ChunkCache.ChunkPos pos) {
        this.lastChunkPos = pos;
        this.user.sendSilent(new WrapperPlayServerUpdateViewPosition(pos.getX(), pos.getZ()));
    }

    public static int floor(double p_76128_0_) {
        int i = (int)p_76128_0_;
        return p_76128_0_ < (double)i ? i - 1 : i;
    }

    public void onSpawn(Dimension dimension, Location location, GameMode gameMode) {
        this.user.sendSilent(new WrapperPlayServerRespawn(
                dimension,
                "slime-replay-in-dev",
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
        //double x, double y, double z, float yaw, float pitch, byte flags, int teleportId, boolean dismountVehicle)
        this.user.sendSilent(new WrapperPlayServerPlayerPositionAndLook(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                (byte) 0,
                -1,
                true
        ));
        this.teleportQueues.add(new TeleportQueue(-1, location));
        this.dimension = dimension;
    }

    public BlockChangesData setBlocks(ReplayUpdateMultipleBlock blockData) {
        BlockChangesData changesData = new BlockChangesData();
        for (ReplayUpdateMultipleBlock.BlockClazz clazz : blockData.getBlocks()) {
            WrappedBlockState blockState = localSetBlock(clazz.getPos(), clazz.getBlockId());
            if (blockState != null) {
                changesData.addBlock(new ReplayUpdateMultipleBlock.BlockClazz(clazz.getPos(), blockState.getGlobalId()));
            }
        }
        this.user.sendSilent(blockData);
        return changesData;
    }


    public WrappedBlockState setBlock(ReplayUpdateBlock blockData) {
        return setBlock(blockData.getBlockPos(), blockData.getBlockId());
    }

    public WrappedBlockState setBlock(Vector3i pos, int blockId) {
        WrappedBlockState blockState = localSetBlock(pos, blockId);
        if (blockState != null && this.sentChunks.contains(new ChunkCache.ChunkPos(pos.getX() >> 4, pos.getZ() >> 4))) {
            this.user.sendSilent(new WrapperPlayServerBlockChange(pos, blockId));
        }
        return blockState;
    }

    public WrappedBlockState localSetBlock(Vector3i blockPos, int blockId) {
        ReplayChunkData chunkData = getChunk(new ChunkCache.ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4));
        if (chunkData == null)
            return null;
        BaseChunk[] baseChunks = chunkData.getColumn().getChunks();
        int y = blockPos.getY() >> 4;
        if (baseChunks.length <= y || y < 0)
            return null;
        BaseChunk baseChunk = baseChunks[y];
        if (baseChunk == null) {
            baseChunk = BaseChunk.create();
        }
        WrappedBlockState klas = baseChunk.get(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF);
        baseChunk.set(blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF, blockId);
        return klas;
    }



    public void sentEntity() {

    }

    public void nextChain(long millis) {
        this.millis += millis;
        while (loopNext()) {
            this.current = this.current.next();
            this.current.send(this);
        }
        if (this.current != null && !this.current.hasNext()) { // Last millis
            this.millis = this.current.getMillis();
        }
    }

    private boolean loopNext() {
        return this.current != null && this.current.hasNext() && this.millis > this.current.next().getMillis();
    }

    public void backChain(long millis) {
        ReplayChain current = this.current;
        List<ReplayChain> collectedChain = new ArrayList<>(); //TODO:
        while (millis > current.getMillis()) {
            collectedChain.add(current);
            current = current.back();
        }
        this.current = current;
    }

    public long getMillis() {
        return millis;
    }

    private static int getChunkDistance(ChunkCache.ChunkPos chunkPos, int x, int z) {
        return Math.max(Math.abs(chunkPos.getX() - x), Math.abs(chunkPos.getZ() - z));
    }

    public static class TeleportQueue {
        private final int teleportId;
        private final Location playerPos;

        public TeleportQueue(int teleportId, Location playerPos) {
            this.teleportId = teleportId;
            this.playerPos = playerPos;
        }

        public int getTeleportId() {
            return teleportId;
        }

        public Location getPlayerPos() {
            return playerPos;
        }
    }

    public static class BlockChangesData {
        private final List<ReplayUpdateMultipleBlock.BlockClazz> blocks = new ArrayList<>();

        public BlockChangesData() {
        }

        public void addBlock(ReplayUpdateMultipleBlock.BlockClazz state) {
            this.blocks.add(state);
        }

        public List<ReplayUpdateMultipleBlock.BlockClazz> getBlocks() {
            return blocks;
        }
    }
}