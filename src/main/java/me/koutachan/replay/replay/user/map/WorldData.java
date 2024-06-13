package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.world.Dimension;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.ReplayPlayerRespawnData;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.ReplayUser;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

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

    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = new ArrayList<>();
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerRespawn(dimension, "slime-replay", Difficulty.NORMAL, 0, GameMode.CREATIVE, GameMode.CREATIVE, false, false, WrapperPlayServerRespawn.KEEP_NOTHING, null, null)));
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.START_LOADING_CHUNKS, 0.0F)));
        Location location = user.getPlayer().getLocation();
        //
        return packets;
    }
}