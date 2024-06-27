package me.koutachan.replay.replay.user.replay.chain;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.ReplayUpdateBlock;
import me.koutachan.replay.replay.packet.in.ReplayUpdateMultipleBlock;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.ChunkCache;
import me.koutachan.replay.replay.user.map.data.PacketEntity;
import me.koutachan.replay.replay.user.replay.chain.impl.ReplayStartDataChain;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReplayRunnerHandler {
    private final ReplayUser user;

    private final List<ReplayChunk> currentChunks = Collections.synchronizedList(new ArrayList<>());
    private final List<PacketEntity> currentEntities = new ArrayList<>();
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
        return this.currentEntities.stream()
                .filter(entity -> entity.getEntityId() == entityId)
                .findFirst()
                .orElse(null);
    }

    public void handleChunkRadius(int chunkRadius) {
        this.chunkRadius = chunkRadius;
    }

    public void handleChunk(ReplayChunkData chunkData) {
        handleChunk(new ReplayChunk(this.user, chunkData));
    }

    public void handleChunk(ReplayChunk chunk) {
        this.currentChunks.add(chunk);
        if (canSendChunks() && this.lastChunkPos != null && this.chunkRadius >= getChunkDistance(this.lastChunkPos, chunk.getX(), chunk.getZ())) {
            chunk.load();
        }
    }

    public boolean hasChunk(ReplayChunkData chunkData) {
        return hasChunk(chunkData.getX(), chunkData.getZ());
    }

    public boolean hasChunk(int x, int z) {
        return this.currentChunks.stream().anyMatch(chunk -> chunk.getX() == x && chunk.getZ() == z);
    }

    public boolean hasSentChunk(int x, int z) {
        ReplayChunk chunk = getChunk(x, z);
        return chunk != null && chunk.isLoaded();
    }

    public ReplayChunk getChunk(ChunkCache.ChunkPos pos) {
        return getChunk(pos.getX(), pos.getZ());
    }

    public ReplayChunk getChunk(int x, int z) {
        return this.currentChunks.stream()
                .filter(chunk -> chunk.getX() == x && chunk.getZ() == z)
                .findFirst()
                .orElse(null);
    }

    public void removeChunk(int x, int z) {
        this.currentChunks.removeIf(chunk -> {
            if (chunk.getX() == x && chunk.getZ() == z) {
                chunk.unload();
                return true;
            }
            return false;
        });
    }

    public void onMove(Location location) {
        if (this.playerPos == null || !playerPos.getPosition().equals(location.getPosition())) {
            this.playerPos = location;
            this.move();
        }
    }

    public ChunkCache.ChunkPos getChunkPos(Location location) {
        return new ChunkCache.ChunkPos(floor(location.getX()) >> 4, floor(location.getZ()) >> 4);
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
        List<ReplayChunk> loadedChunk = collectLoadedChunk();
        for (int x = chunkX - this.chunkRadius; x <= chunkX + this.chunkRadius; ++x) {
            for (int z = chunkZ - this.chunkRadius; z <= chunkZ + this.chunkRadius; ++z) {
                ReplayChunk chunk = getChunk(x, z);
                if (chunk != null && !loadedChunk.remove(chunk)) {
                    chunk.load();
                }
            }
        }
        loadedChunk.forEach(ReplayChunk::unload);
    }

    private List<ReplayChunk> collectLoadedChunk() {
        return this.currentChunks.stream()
                .filter(ReplayChunk::isLoaded)
                .collect(Collectors.toList());
    }

    public void onCompleteTeleport(int teleportId) {
        TeleportQueue predictedQueue = this.teleportQueues.remove(0);
        if (predictedQueue == null || predictedQueue.teleportId != teleportId) {
            this.user.sendSilent(new WrapperPlayServerDisconnect(Component.text("The observed teleport ID is inconsistent with the predicted value (potential cheating?)")));
            this.user.closeConnection();
            return;
        }
        if (canSendChunks()) {
            onMove(predictedQueue.getPlayerPos());
        }
    }

    public boolean canSendChunks() {
        return this.teleportQueues.isEmpty();
    }

    public void updateChunkPos(ChunkCache.ChunkPos pos) {
        this.lastChunkPos = pos;
        this.user.sendSilent(new WrapperPlayServerUpdateViewPosition(pos.getX(), pos.getZ()));
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
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
        teleportTo(location);
        this.dimension = dimension;
    }

    public void teleportTo(Location location) {
        this.user.sendSilent(new WrapperPlayServerPlayerPositionAndLook(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                (byte) 0,
                ++this.localId,
                true
        ));
        this.teleportQueues.add(new TeleportQueue(this.localId, location));
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
        if (blockState != null && hasSentChunk(pos.getX() >> 4, pos.getZ() >> 4)) { //TODO: refactor
            this.user.sendSilent(new WrapperPlayServerBlockChange(pos, blockId));
        }
        return blockState;
    }

    public WrappedBlockState localSetBlock(Vector3i blockPos, int blockId) {
        ReplayChunk chunkData = getChunk(new ChunkCache.ChunkPos(blockPos.getX() >> 4, blockPos.getZ() >> 4));
        if (chunkData == null)
            return null;
        BaseChunk[] baseChunks = chunkData.getBaseChunk();
        int y = (blockPos.getY() - this.user.getMinHeight()) >> 4;
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
            List<PacketWrapper<?>> packetWrappers = this.current.send(this);
            if (packetWrappers != null) {
                this.user.sendSilent(packetWrappers);
            }
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

    public ReplayUser getUser() {
        return user;
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