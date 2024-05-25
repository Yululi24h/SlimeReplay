package me.koutachan.replay.replay.user;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import me.koutachan.replay.replay.ReplayPacket;
import me.koutachan.replay.replay.user.map.ChunkMap;
import me.koutachan.replay.replay.user.map.EntitiesMap;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ReplayUser {
    private final User user;
    private final Player player;
    private ReplayHook hook;

    private final EntitiesMap entities = new EntitiesMap();
    private final ChunkMap chunk = new ChunkMap();

    public ReplayUser(User user, Object player) {
        this.user = user;
        this.player = (Player) player;
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

    public boolean isRecording() {
        return hook != null;
    }

    public void onPacket(ReplayPacket packet) {
        entities.onPacket(packet);
        chunk.onPacket(packet);
        if (isRecording()) {
            hook.onPacket(packet);
        }
    }
}