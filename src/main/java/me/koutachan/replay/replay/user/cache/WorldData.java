package me.koutachan.replay.replay.user.cache;

import com.github.retrooper.packetevents.protocol.world.Dimension;
import me.koutachan.replay.replay.packet.in.ReplayPlayerRespawnData;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;

public class WorldData {
    private final ReplayUser user;
    private Dimension dimension;

    public WorldData(ReplayUser user) {
        this.user = user;
        this.dimension = user.getDimension();
    }

    public void onPacket(ReplayWrapper<?> packet) {
        if (packet instanceof ReplayPlayerRespawnData) {
            ReplayPlayerRespawnData data = (ReplayPlayerRespawnData) packet;
            if (data.isWorldChanged(this.dimension)) {
                this.user.getChunk().clearCache();
            }
            this.user.getEntities().clearCache();
            this.dimension = data.getDimension();
        }
    }

    public Dimension getDimension() {
        return dimension;
    }
}