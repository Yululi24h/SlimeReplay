package me.koutachan.replay.replay.user.map;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.impl.ReplayPacketImpl;
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

    public void onPacket(ReplayPacket packet) {
        PacketWrapper<?> packetWrapper = packet.toPacket();
        PacketTypeCommon packetType = packetWrapper.getPacketTypeData().getPacketType();
        if (packetType == PacketType.Play.Server.RESPAWN) {
            WrapperPlayServerRespawn respawn = (WrapperPlayServerRespawn) packetWrapper;
            Dimension dimension = respawn.getDimension();
            if (!this.dimension.getAttributes().equals(dimension.getAttributes()) ||
                    !this.dimension.getDimensionName().equals(dimension.getDimensionName()) ||
                    this.dimension.getId() != dimension.getId()) {
                // Clear chunks.
                user.getChunk().clear();
            }
            // Clear Entities.
            user.getEntities().clear();
            this.dimension = dimension;
        }
    }

    public List<ReplayPacket> toPacket() {
        List<ReplayPacket> packets = new ArrayList<>();
        packets.add(new ReplayPacketImpl(new WrapperPlayServerRespawn(dimension, "slime-replay", Difficulty.NORMAL, 0, GameMode.CREATIVE, GameMode.CREATIVE, false, false, WrapperPlayServerRespawn.KEEP_NOTHING, null, null)));
        //packets.add(new ReplayPacketImpl(new WrapperPlayServerChangeGameState(WrapperPlayServerChangeGameState.Reason.START_LOADING_CHUNKS, 0.0F)));
        Location location = user.getPlayer().getLocation();
        //
        return packets;
    }
}