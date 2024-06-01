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

package me.koutachan.replay.replay.packet.in.packetevents;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;

public class WrapperPlayServerUpdateLight extends PacketWrapper<WrapperPlayServerUpdateLight> {
    private int x;
    private int z;
    private LightData lightData;

    public WrapperPlayServerUpdateLight(PacketSendEvent event) {
        super(event);
    }

    public WrapperPlayServerUpdateLight(int x, int z, LightData lightData) {
        super(PacketType.Play.Server.UPDATE_LIGHT);
        this.x = x;
        this.z = z;
        this.lightData = lightData;
    }

    @Override
    public void read() {
        this.x = readVarInt();
        this.z = readVarInt();
        this.lightData = new LightData();
        this.lightData.read(this);
    }

    @Override
    public void write() {
        writeVarInt(this.x);
        writeVarInt(this.z);
        this.lightData.write(this);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public LightData getLightData() {
        return lightData;
    }

    public void setLightData(LightData lightData) {
        this.lightData = lightData;
    }

    @Override
    public void copy(WrapperPlayServerUpdateLight wrapper) {
        x = wrapper.x;
        z = wrapper.z;
        lightData = wrapper.lightData.clone();
    }
}