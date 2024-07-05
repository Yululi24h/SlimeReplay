/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2022 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReplayPlayerInfo extends ReplayWrapper<ReplayPlayerInfo> {
    private Action action;
    private List<PlayerData> playerData;

    public ReplayPlayerInfo(ServerVersion version, Object byteBuf) {
        super(version, byteBuf);
    }

    public ReplayPlayerInfo(Action action, List<PlayerData> playerData) {
        this.action = action;
        this.playerData = playerData;
    }

    @Override
    public void read() {
        this.action = Action.getByOrdinal(readVarInt());
        int size = readVarInt();
        this.playerData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            switch (this.action) {
                case ADD_PLAYER: {
                    UserProfile profile = new UserProfile(readUUID(), readString(16));
                    for (int j = 0; j < readVarInt(); j++) {
                        String name = readString(Short.MAX_VALUE);
                        String value = readString(Short.MAX_VALUE);
                        String signature = readOptional(PacketWrapper::readString);
                        profile.getTextureProperties().add(new TextureProperty(name, value, signature));
                    }
                    this.playerData.add(new PlayerData(
                            profile,
                            readGameMode(),
                            readOptional(PacketWrapper::readComponent)
                    ));
                }
                case UPDATE_GAME_MODE: {
                    this.playerData.add(new PlayerData(
                            new UserProfile(readUUID(), null),
                            readGameMode(),
                            null
                    ));
                }
                case UPDATE_DISPLAY_NAME: {
                    this.playerData.add(new PlayerData(
                            new UserProfile(readUUID(), null),
                            null,
                            readOptional(PacketWrapper::readComponent)
                    ));
                }
                case REMOVE_PLAYER: {
                    this.playerData.add(new PlayerData(
                            new UserProfile(readUUID(), null)
                    ));
                }
            }
        }
    }

    @Override
    public void write() {
        writeVarInt(this.action.ordinal());
        writeVarInt(this.playerData.size());
        for (PlayerData playerData : this.playerData) {
            writeUUID(playerData.getProfile().getUUID());
            switch (this.action) {
                case ADD_PLAYER: {
                    writeString(playerData.getProfile().getName());
                    writeList(playerData.getProfile().getTextureProperties(), (packet, texture) -> {
                        packet.writeString(texture.getName());
                        packet.writeString(texture.getValue());
                        packet.writeOptional(texture.getSignature(), PacketWrapper::writeString);
                    });
                    writeGameMode(playerData.getGameMode());
                    writeOptional(playerData.getDisplayName(), PacketWrapper::writeComponent);
                }
                case UPDATE_GAME_MODE: {
                    writeGameMode(playerData.getGameMode());
                }
                case UPDATE_DISPLAY_NAME: {
                    writeOptional(playerData.getDisplayName(), PacketWrapper::writeComponent);
                }
                case REMOVE_PLAYER: {

                }
            }
        }
    }

    public Action getAction() {
        return action;
    }

    public List<PlayerData> getPlayerData() {
        return playerData;
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return false;
    }

    @Override
    public List<PacketWrapper<?>> getPackets() {
        return Collections.singletonList(toPacketEvent());
    }

    @Override
    public List<PacketWrapper<?>> getInvertedPackets() {
        return null;
    }

    public WrapperPlayServerPlayerInfo toPacketEvent() {
        List<WrapperPlayServerPlayerInfo.PlayerData> playerDataList = new ArrayList<>(this.playerData.size());
        for (PlayerData playerData : this.playerData) {
            playerDataList.add(new WrapperPlayServerPlayerInfo.PlayerData(playerData.getDisplayName(), playerData.getProfile(), playerData.getGameMode(), 0));
        }
        return new WrapperPlayServerPlayerInfo(this.action.toPacketAction(), playerDataList);
    }

    public static ReplayPlayerInfo fromPacketEvent(WrapperPlayServerPlayerInfo wrapper) {
        if (wrapper.getAction() == null || wrapper.getAction() == WrapperPlayServerPlayerInfo.Action.UPDATE_LATENCY)
            return null;
        List<PlayerData> playerDataList = new ArrayList<>(wrapper.getPlayerDataList().size());
        for (WrapperPlayServerPlayerInfo.PlayerData playerData : wrapper.getPlayerDataList()) {
            playerDataList.add(new PlayerData(playerData.getUserProfile(), playerData.getGameMode(), playerData.getDisplayName()));
        }
        return new ReplayPlayerInfo(Action.getByPacketAction(wrapper.getAction()), playerDataList);
    }

    public enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY, // TODO: REMOVE UPDATE_LATENCY BUT CURRENT NOT.
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;

        public final static Action[] values = values();

        public WrapperPlayServerPlayerInfo.Action toPacketAction() {
            return WrapperPlayServerPlayerInfo.Action.VALUES[ordinal()];
        }

        public static Action getByOrdinal(int ordinal) {
            return values[ordinal];
        }

        public static Action getByPacketAction(WrapperPlayServerPlayerInfo.Action action) {
            return getByOrdinal(action.ordinal());
        }
    }

    public static class PlayerData {
        // Do we need to save the ping of the player here?
        // It may be useful if the player is hacking, but I don't think it will help much...
        private final UserProfile profile;
        private GameMode gameMode;
        private final Component displayName;

        public PlayerData(UserProfile profile) {
            this(profile, null, null);
        }

        public PlayerData(UserProfile profile, GameMode gameMode, Component displayName) {
            this.profile = profile;
            this.gameMode = gameMode;
            this.displayName = displayName;
        }

        public UserProfile getProfile() {
            return profile;
        }

        public GameMode getGameMode() {
            return gameMode;
        }

        public void setGameMode(GameMode gameMode) {
            this.gameMode = gameMode;
        }

        public Component getDisplayName() {
            return displayName;
        }
    }
}