package me.koutachan.replay;

import com.github.retrooper.packetevents.PacketEvents;
import me.koutachan.replay.api.SlimeReplayAPI;
import me.koutachan.replay.commands.PacketTestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlimeReplay extends JavaPlugin {
    public SlimeReplayAPI replay = new SlimeReplayAPI();

    @Override
    public void onEnable() {
        replay.start(this);
        // Plugin startup logic
        getCommand("packet-test").setExecutor(new PacketTestCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}