package me.koutachan.replay.replay.user.replay.chain;

import me.koutachan.replay.replay.packet.in.ReplayChunkData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerChunkData;
import me.koutachan.replay.replay.user.ReplayUser;
import me.koutachan.replay.replay.user.map.data.PacketEntity;
import me.koutachan.replay.replay.user.map.data.PacketEntitySelf;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReplayRunnerHandler {
    private ReplayUser user;

    private final List<WrapperPlayServerChunkData> sentChunks = new ArrayList<>();
    private final List<ReplayChunkData> currentChunks = new ArrayList<>();

    private final List<PacketEntity> sentEntities = new ArrayList<>();


    private ReplayChain current;
    private PacketEntitySelf self;

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

    public class ReconizedData {

    }
}