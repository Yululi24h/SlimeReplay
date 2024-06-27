package me.koutachan.replay.replay.user;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.packet.in.ReplayWrapper;
import me.koutachan.replay.replay.user.map.ChunkCache;
import me.koutachan.replay.replay.user.map.EntitiesCache;
import me.koutachan.replay.replay.user.map.WorldData;
import me.koutachan.replay.replay.user.record.RecordRunner;
import me.koutachan.replay.replay.user.replay.ReplayRunner;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class ReplayUser {
    private final User user;
    private final Player player;

    private final WorldData world;
    private final EntitiesCache entities;
    private final ChunkCache chunk ;

    private RecordRunner recordRunner;
    private ReplayRunner replayRunner;

    public ReplayUser(User user, Object player) {
        this.user = user;
        this.player = (Player) player;
        this.world = new WorldData(this);
        this.entities = new EntitiesCache(this);
        this.chunk = new ChunkCache(this);
    }

    public User getUser() {
        return user;
    }

    public Player getPlayer() {
        return player;
    }

    public World getBukkitWorld() {
        return player.getWorld();
    }

    public ChunkCache getChunk() {
        return chunk;
    }

    public EntitiesCache getEntities() {
        return entities;
    }

    public WorldData getWorld() {
        return world;
    }

    public Dimension getDimension() {
        return user.getDimension();
    }

    public void sendSilent(PacketWrapper<?> wrapper) {
        user.sendPacketSilently(wrapper);
    }

    public void sendSilent(List<PacketWrapper<?>> wrappers) {
        for (PacketWrapper<?> wrapper : wrappers) {
            sendSilent(wrapper);
        }
    }

    public void sendSilent(ReplayPacket packet) {
    //    user.sendPacketSilently(packet.toPacket());
    }

    public boolean isRecording() {
        return recordRunner != null && recordRunner.isRecording();
    }

    public boolean isReplaying() {
        return replayRunner != null;
    }

    public void startRecord(File file) {
        this.recordRunner = RecordRunner.ofFile(this, file);
        this.recordRunner.start();
    }

    public void startReplay(File file) {
        this.replayRunner = ReplayRunner.ofFile(this, file);
        this.replayRunner.start();
    }

    public void stopRecord() {
        if (this.recordRunner != null) {
            this.recordRunner.stop();
            this.recordRunner = null;
        }
    }

    public void sendMessage(String message) {
        user.sendMessage(Component.text(message));
    }

    public void onPacket(ReplayWrapper<?> packet) {
        world.onPacket(packet);
        entities.onPacket(packet);
        chunk.onPacket(packet);
        if (isRecording()) {
            recordRunner.onPacket(packet);
        }
    }

    public int getMinHeight() {
        return this.user.getMinWorldHeight();
    }

    public void closeConnection() {
        this.user.closeConnection();
    }

    public RecordRunner getRecordRunner() {
        return recordRunner;
    }

    public ReplayRunner getReplayRunner() {
        return replayRunner;
    }

    public int getEntityId() {
        return user.getEntityId();
    }

    public void shutdown() {

    }
}