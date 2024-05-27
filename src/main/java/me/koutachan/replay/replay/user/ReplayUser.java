package me.koutachan.replay.replay.user;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Dimension;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.ReplayPacket;
import me.koutachan.replay.replay.user.map.ChunkMap;
import me.koutachan.replay.replay.user.map.EntitiesData;
import me.koutachan.replay.replay.user.map.WorldData;
import me.koutachan.replay.replay.user.record.RecordRunner;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;

public class ReplayUser {
    private final User user;
    private final Player player;

    private final WorldData world;
    private final EntitiesData entities;
    private final ChunkMap chunk = new ChunkMap();

    private RecordRunner recordRunner;

    public ReplayUser(User user, Object player) {
        this.user = user;
        this.player = (Player) player;
        this.world = new WorldData(this);
        this.entities = new EntitiesData(this);
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

    public ChunkMap getChunk() {
        return chunk;
    }

    public EntitiesData getEntities() {
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

    public void sendSilent(ReplayPacket packet) {
        if (!packet.isGenerated()) {
            user.sendPacketSilently(packet.toPacket());
        }
    }

    public boolean isRecording() {
        return recordRunner != null && recordRunner.isRecording();
    }

    public void startRecord(File file) {
        this.recordRunner = RecordRunner.ofFile(this, file);
        this.recordRunner.start();
    }

    public void onPacket(ReplayPacket packet) {
        world.onPacket(packet);
        entities.onPacket(packet);
        chunk.onPacket(packet);
        if (isRecording()) {
            recordRunner.onPacket(packet);
        }
    }
}