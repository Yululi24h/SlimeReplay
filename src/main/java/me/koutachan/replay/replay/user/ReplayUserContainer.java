package me.koutachan.replay.replay.user;

import com.github.retrooper.packetevents.protocol.player.User;
import me.koutachan.replay.replay.user.ReplayUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplayUserContainer {
    public static Map<UUID, ReplayUser> USERS = new HashMap<>();

    public static ReplayUser getUser(User user) {
        return getUser(user.getUUID());
    }

    public static ReplayUser getUser(UUID uuid) {
        return USERS.get(uuid);
    }

    public static boolean hasUser(User user) {
        return hasUser(user.getUUID());
    }

    public static boolean hasUser(UUID uuid) {
        return USERS.containsKey(uuid);
    }

    public static void registerUser(User user, Object player) {
        USERS.put(user.getUUID(), new ReplayUser(user, player));
    }

    public static void unregisterUser(User user) {
        USERS.remove(user.getUUID());
    }
}