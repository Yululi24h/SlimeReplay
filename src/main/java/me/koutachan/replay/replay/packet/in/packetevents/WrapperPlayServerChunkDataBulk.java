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
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.NetworkChunkData;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_7.Chunk_v1_7;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
import com.github.retrooper.packetevents.protocol.world.chunk.reader.impl.ChunkReader_v1_7;
import com.github.retrooper.packetevents.protocol.world.chunk.reader.impl.ChunkReader_v1_8;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class WrapperPlayServerChunkDataBulk extends PacketWrapper<WrapperPlayServerChunkDataBulk> {
    private int[] x;
    private int[] z;
    private BaseChunk[][] chunks;
    private byte[][] biomeData;

    public WrapperPlayServerChunkDataBulk(PacketSendEvent event) {
        super(event);
    }

    public WrapperPlayServerChunkDataBulk() {
        super(PacketType.Play.Server.MAP_CHUNK_BULK);
    }

    public WrapperPlayServerChunkDataBulk(int[] x, int[] z, BaseChunk[][] chunks, byte[][] biomeData) {
        super(PacketType.Play.Server.MAP_CHUNK_BULK);
        this.x = x;
        this.z = z;
        this.chunks = chunks;
        this.biomeData = biomeData;
    }

    public void read() {
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8)) {
            this.read_1_8();
        } else {
            this.read_1_7();
        }
    }

    private void read_1_8() {
        boolean skylight = this.readBoolean();
        int columns = this.readVarInt();
        this.x = new int[columns];
        this.z = new int[columns];
        this.chunks = new BaseChunk[columns][];
        this.biomeData = new byte[columns][];
        NetworkChunkData[] data = new NetworkChunkData[columns];

        int column;
        for(column = 0; column < columns; ++column) {
            this.x[column] = this.readInt();
            this.z[column] = this.readInt();
            int mask = this.readUnsignedShort();
            int chunks = Integer.bitCount(mask);
            int length = chunks * 10240 + (skylight ? chunks * 2048 : 0);
            byte[] dat = new byte[length];
            data[column] = new NetworkChunkData(mask, true, skylight, dat);
        }

        for(column = 0; column < columns; ++column) {
            data[column].setData(this.readBytes(data[column].getData().length));
            BitSet mask = BitSet.valueOf(new long[]{(long)data[column].getMask()});
            BaseChunk[] chunkData = (new ChunkReader_v1_8()).read(this.user.getDimension(), mask, (BitSet)null, true, skylight, false, 16, data[column].getData(), (NetStreamInput)null);
            this.chunks[column] = chunkData;
            this.biomeData[column] = this.readBytes(256);
        }

    }

    private void read_1_7() {
        short columns = this.readShort();
        int deflatedLength = this.readInt();
        boolean skylight = this.readBoolean();
        byte[] deflatedBytes = this.readBytes(deflatedLength);
        byte[] inflated = new byte[196864 * columns];
        Inflater inflater = new Inflater();
        inflater.setInput(deflatedBytes, 0, deflatedLength);

        label99: {
            try {
                inflater.inflate(inflated);
                break label99;
            } catch (DataFormatException var21) {
                (new IOException("Bad compressed data format")).printStackTrace();
            } finally {
                inflater.end();
            }

            return;
        }

        this.x = new int[columns];
        this.z = new int[columns];
        this.chunks = new BaseChunk[columns][];
        this.biomeData = new byte[columns][];
        int pos = 0;

        for(int count = 0; count < columns; ++count) {
            int x = this.readInt();
            int z = this.readInt();
            BitSet chunkMask = BitSet.valueOf(new long[]{(long)this.readUnsignedShort()});
            BitSet extendedChunkMask = BitSet.valueOf(new long[]{(long)this.readUnsignedShort()});
            int chunks = 0;
            int extended = 0;

            int length;
            for(length = 0; length < 16; ++length) {
                chunks += chunkMask.get(length) ? 1 : 0;
                extended += extendedChunkMask.get(length) ? 1 : 0;
            }

            length = 8192 * chunks + 256 + 2048 * extended;
            if (skylight) {
                length += 2048 * chunks;
            }

            byte[] dat = new byte[length];
            System.arraycopy(inflated, pos, dat, 0, length);
            BaseChunk[] chunkData = (new ChunkReader_v1_7()).read(this.user.getDimension(), chunkMask, extendedChunkMask, true, skylight, false, 16, dat, (NetStreamInput)null);
            byte[] biomeDataBytes = Arrays.copyOfRange(dat, dat.length - 256, dat.length);
            this.x[count] = x;
            this.z[count] = z;
            this.chunks[count] = chunkData;
            this.biomeData[count] = biomeDataBytes;
            pos += length;
        }

    }

    public void write() {
        if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_8)) {
            this.write_1_8();
        } else {
            this.write_1_7();
        }

    }

    private void write_1_8() {
        boolean skylight = false;
        NetworkChunkData[] data = new NetworkChunkData[this.chunks.length];

        int column;
        for(column = 0; column < this.chunks.length; ++column) {
            data[column] = ChunkReader_v1_8.chunksToData((Chunk_v1_8[])this.chunks[column], this.biomeData[column]);
            if (data[column].hasSkyLight()) {
                skylight = true;
            }
        }

        this.writeBoolean(skylight);
        this.writeVarInt(this.chunks.length);

        for(column = 0; column < this.x.length; ++column) {
            this.writeInt(this.x[column]);
            this.writeInt(this.z[column]);
            this.writeShort(data[column].getMask());
        }

        for(column = 0; column < this.x.length; ++column) {
            this.writeBytes(data[column].getData());
        }

    }

    private void write_1_7() {
        int[] chunkMask = new int[this.chunks.length];
        int[] extendedChunkMask = new int[this.chunks.length];
        int pos = 0;
        byte[] bytes = new byte[0];
        boolean skylight = false;

        for(int count = 0; count < this.chunks.length; ++count) {
            BaseChunk[] column = this.chunks[count];
            NetworkChunkData data = ChunkReader_v1_7.chunksToData((Chunk_v1_7[])column, this.biomeData[count]);
            if (bytes.length < pos + data.getData().length) {
                byte[] newArray = new byte[pos + data.getData().length];
                System.arraycopy(bytes, 0, newArray, 0, bytes.length);
                bytes = newArray;
            }

            if (data.hasSkyLight()) {
                skylight = true;
            }

            System.arraycopy(data.getData(), 0, bytes, pos, data.getData().length);
            pos += data.getData().length;
            chunkMask[count] = data.getMask();
            extendedChunkMask[count] = data.getExtendedChunkMask();
        }

        Deflater deflater = new Deflater(-1);
        byte[] deflatedData = new byte[pos];

        int deflatedLength;
        try {
            deflater.setInput(bytes, 0, pos);
            deflater.finish();
            deflatedLength = deflater.deflate(deflatedData);
        } finally {
            deflater.end();
        }

        this.writeShort(this.chunks.length);
        this.writeInt(deflatedLength);
        this.writeBoolean(skylight);

        int count;
        for(count = 0; count < deflatedLength; ++count) {
            this.writeByte(deflatedData[count]);
        }

        for(count = 0; count < this.chunks.length; ++count) {
            this.writeInt(this.x[count]);
            this.writeInt(this.z[count]);
            this.writeShort((short)(chunkMask[count] & '\uffff'));
            this.writeShort((short)(extendedChunkMask[count] & '\uffff'));
        }

    }

    public int[] getX() {
        return this.x;
    }

    public int[] getZ() {
        return this.z;
    }

    public BaseChunk[][] getChunks() {
        return this.chunks;
    }

    public byte[][] getBiomeData() {
        return this.biomeData;
    }
}