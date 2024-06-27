package me.koutachan.replay.api;

import com.github.retrooper.packetevents.PacketEvents;
import me.koutachan.replay.packetevents.PacketListener;
import org.bukkit.plugin.Plugin;

public class SlimeReplayAPI {
    public static SlimeReplayAPI INSTANCE;

    public void start(Plugin plugin) {
        /*PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(true)
                .bStats(false)
                .fullStackTrace(true)
                .kickOnPacketException(false)
                .debug(true)
                .checkForUpdates(false);
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
        PacketEvents.getAPI().init();*/

        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
        PacketEvents.getAPI().getSettings().fullStackTrace(true);
        INSTANCE = this;
    }

    public static SlimeReplayAPI getInstance() {
        return INSTANCE;
    }
}