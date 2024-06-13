package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.world.Difficulty;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRespawn;

import java.util.ArrayList;
import java.util.List;

public class ReplayPlayerRespawnData extends ReplayWrapper<ReplayPlayerRespawnData> {
    private Dimension dimension;
    private GameMode gameMode;
    private byte keptData;
    /* private Difficulty difficulty; */

    public ReplayPlayerRespawnData(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayPlayerRespawnData(PacketSendEvent event) {
        WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn(event);
        this.dimension = respawn.getDimension();
        this.gameMode = respawn.getGameMode();
        this.keptData = respawn.getKeptData();
    }

    public ReplayPlayerRespawnData(Dimension dimension, GameMode gameMode, byte keptData) {
        super();
        this.dimension = dimension;
        this.gameMode = gameMode;
        this.keptData = keptData;
    }

    @Override
    public void read() {
        this.dimension = readDimension();
        this.gameMode = readGameMode();
        this.keptData = readByte();
    }

    @Override
    public void write() {
        writeDimension(this.dimension);
        writeGameMode(this.gameMode);
        writeByte(this.keptData);
    }

    public Dimension getDimension() {
        return dimension;
    }

    public boolean isWorldChanged(Dimension dimension) {
        return !this.dimension.getAttributes().equals(this.dimension.getAttributes())
                || !this.dimension.getDimensionName().equals(dimension.getDimensionName())
                || this.dimension.getId() != dimension.getId();
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        List<PacketWrapper<?>> packets = new ArrayList<>();
        packets.add(new WrapperPlayServerRespawn(
                this.dimension,
                "Slime-Replay-InDev",
                Difficulty.NORMAL, //TODO: I don't think we need to set up a difficulty here,
                0L,
                this.gameMode,
                this.gameMode,
                false,
                false,
                this.keptData, //TODO: Is this really needed? //TODO: I don't need that. but we're track here.
                null,
                null
        ));
        return packets;
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }
}