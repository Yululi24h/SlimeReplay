package me.koutachan.replay.api;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.Plugin;

public class SlimeReplayAPI {
    public void start(Plugin plugin) {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(true)
                .bStats(false)
                .checkForUpdates(false);
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();
    }
}
