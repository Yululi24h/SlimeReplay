package me.koutachan.replay.replay.user.replay.chain;

import me.koutachan.replay.replay.packet.in.ReplayChunkData;

import java.util.ArrayList;
import java.util.List;

public class ReplayChunk {
    private final ReplayChunkData chunkData;
    private final List<ReplayEntity> currentChunkEntities = new ArrayList<>();

    public ReplayChunk(ReplayChunkData chunkData) {
        this.chunkData = chunkData;
    }

    public ReplayChunkData getChunkData() {
        return chunkData;
    }

    public void addEntity(ReplayEntity replayEntity) {
        this.currentChunkEntities.add(replayEntity);
        replayEntity.setReplayChunk(this);
    }

    public void removeEntity(ReplayEntity replayEntity) {
        if (this.currentChunkEntities.remove(replayEntity)) {
            replayEntity.setReplayChunk(null);
        }
    }

    public void unloadEntities() {
        for (ReplayEntity replayEntity : this.currentChunkEntities) {
            replayEntity.unload();
        }
        this.currentChunkEntities.clear();
    }
}