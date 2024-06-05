package me.koutachan.replay.utils;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.NibbleArray3d;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.koutachan.replay.replay.packet.in.packetevents.LightData;
import me.koutachan.replay.replay.packet.in.packetevents.WrapperPlayServerChunkData;

import java.lang.reflect.Field;
import java.util.*;

public class ChunkUtils {
    private final static byte[] ZERO = new byte[2048];

    private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
    private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
    public static final int BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    public static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;

    public static List<PacketWrapper<?>> formatChunk(WrapperPlayServerChunkData chunkData, ServerVersion from, ServerVersion to) {
        if (from == to) // Fixed.
            return Collections.singletonList(chunkData);
        LightTransform[] transforms = from.isNewerThanOrEquals(ServerVersion.V_1_18)
                ? collectLightData(chunkData.getLightData(), chunkData.getBaseChunk())
                : collectLightData(chunkData.getBaseChunk());
        if (to.isNewerThanOrEquals(ServerVersion.V_1_18)) {
            return toLightData(chunkData, transforms, from, to);
        } else if (to.isNewerThanOrEquals(ServerVersion.V_1_13)) {

        }
        return null;
    }

    public static List<PacketWrapper<?>> toLightData(WrapperPlayServerChunkData chunkData, LightTransform[] transforms, ServerVersion from, ServerVersion to) {
        LightData lightData = new LightData();
        lightData.setTrustEdges(false);
        LightMask skyLight = new LightMask();
        LightMask blockLight = new LightMask();

        Column column = chunkData.getColumn();

        Chunk_v1_18[] chunks = new Chunk_v1_18[transforms.length];
        for (int i = 0; i < transforms.length; i++) {
            LightTransform transform = transforms[i];
            if (transform != null) {
                Chunk_v1_18 chunk = new Chunk_v1_18();
                BaseChunk baseChunk = column.getChunks()[i];
                skyLight.calculate(i, transform.skyLight);
                blockLight.calculate(i, transform.blockLight);
                if (column.hasBiomeData())  {
                    if (column.getBiomeDataInts() != null) {
                        int[] biomeArray = column.getBiomeDataInts();
                        for (int c = 0; c < biomeArray.length; c++) {
                            int br = biomeArray[i];
                            chunk.getBiomeData().set(
                                    c & HORIZONTAL_MASK,
                                    c >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK,
                                    c >> WIDTH_BITS & HORIZONTAL_MASK,
                                    br
                            );
                        }
                    } else {
                        byte[] biomeArray = column.getBiomeDataBytes(); //TODO: FIX 1.13~1.14
                        for (int j = 0; j < 4; j++) {
                            for (int k = 0; k < 4; k++) {
                                int mixX = j * 4;
                                int mixZ = k * 4;
                                int l = biomeArray[mixX << 4 | mixZ] & 255;
                                for (int m = 0; m < 4; m++) {
                                    chunk.getBiomeData().set(j, m, k, l);
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        for (int l = 0; l < 16; l++) {
                            chunk.set(j, k, l, baseChunk.get(from.toClientVersion(), j, k, l));
                        }
                    }
                }
            }
        }
        lightData.setSkyLightMask(skyLight.getLightMask());
        lightData.setEmptySkyLightMask(skyLight.getEmptyMask());
        lightData.setSkyLightArray(skyLight.getLightArray());
        lightData.setBlockLightMask(blockLight.getLightMask());
        lightData.setEmptyBlockLightMask(blockLight.getEmptyMask());
        lightData.setBlockLightArray(blockLight.getLightArray());
        Column newColumn = new Column(
                column.getX(),
                column.getZ(),
                column.isFullChunk(),
                chunks,
                column.getTileEntities(),
                column.getHeightMaps()
        );
        return Collections.singletonList(new WrapperPlayServerChunkData(
                newColumn,
                lightData,
                false
        ));
    }



    public static int[] toIntArray(byte[] byteArray) {
        int[] intArray = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; intArray[i] = byteArray[i++]);
        return intArray;
    }

    public void encodeDataPalette(BaseChunk oldChunk, BaseChunk newChunk, ServerVersion from, ServerVersion to) {


    }

    private static LightTransform[] collectLightData(BaseChunk[] baseChunks) {
        LightTransform[] lightTransforms = new LightTransform[baseChunks.length];
        for (int i = 0; i < baseChunks.length; i++) {
            BaseChunk baseChunk = baseChunks[i];
            if (!(baseChunk instanceof Chunk_v1_9) && !(baseChunk instanceof Chunk_v1_8)) { // I don't know how to support 1.7...
                throw new IllegalStateException();
            }
            lightTransforms[i] = new LightTransform(
                    baseChunk,
                    retrieveNibbleArray3d(baseChunk, "skyLight"),
                    retrieveNibbleArray3d(baseChunk, "blockLight")
            );
        }
        return lightTransforms;
    }

    private static NibbleArray3d retrieveNibbleArray3d(Object obj, String name) {
        try {
            Field nibbleField = obj.getClass().getDeclaredField(name);
            nibbleField.setAccessible(true);
            return (NibbleArray3d) nibbleField.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static LightTransform[] collectLightData(LightData lightData, BaseChunk[] baseChunks) {
        Iterator<byte[]> skyLightIt = iteratorArray(lightData.getSkyLightArray());
        Iterator<byte[]> blockLightIt = iteratorArray(lightData.getBlockLightArray());
        LightTransform[] lightTransforms = new LightTransform[baseChunks.length];
        for (int i = 0; i < baseChunks.length; i++) {
            lightTransforms[i] = new LightTransform(
                    baseChunks[i],
                    getLight(skyLightIt, lightData.getEmptySkyLightMask(), lightData.getSkyLightMask(), i),
                    getLight(blockLightIt, lightData.getEmptyBlockLightMask(), lightData.getBlockLightMask(), i)
            );
        }
        return lightTransforms;
    }

    public static Iterator<byte[]> iteratorArray(byte[][] bytes) {
        return Arrays.stream(bytes).iterator();
    }

    public static byte[] getLight(Iterator<byte[]> it, BitSet emptySet, BitSet lightSet, int index) {
        if (lightSet.get(index))
            return it.next();
        return emptySet.get(index) ? ZERO : null;
    }

    public static class LightMask {
        private final List<byte[]> lightList = new ArrayList<>();
        private final BitSet emptyMask = new BitSet();
        private final BitSet lightMask = new BitSet();

        public LightMask() {

        }

        public void calculate(int index, byte[] lightArray) {
            if (lightArray == ZERO || lightArray == null) {
                emptyMask.set(index);
            } else {
                lightMask.set(index);
                lightList.add(lightArray);
            }
        }

        public BitSet getEmptyMask() {
            return emptyMask;
        }

        public BitSet getLightMask() {
            return lightMask;
        }

        public List<byte[]> getLightList() {
            return lightList;
        }

        public byte[][] getLightArray() {
            return lightList.toArray(new byte[0][]);
        }
    }

    public static class BiomeTransform {
        //private final BaseChunk baseChunk;
    }

    public static class LightTransform {
        private final BaseChunk baseChunk;
        private final byte[] skyLight;
        private final byte[] blockLight;

        public LightTransform(BaseChunk baseChunk, byte[] skyLight, byte[] blockLight) {
            this.baseChunk = baseChunk;
            this.skyLight = skyLight;
            this.blockLight = blockLight;
        }

        public LightTransform(BaseChunk baseChunk, NibbleArray3d skyLight, NibbleArray3d blockLight) {
            this.baseChunk = baseChunk;
            this.skyLight = skyLight != null ? skyLight.getData() : null;
            this.blockLight = blockLight != null ? blockLight.getData() : null;
            //Chunk_v1_9
        }

        public BaseChunk getBaseChunk() {
            return baseChunk;
        }

        public byte[] getSkyLight() {
            return skyLight;
        }

        public byte[] getBlockLight() {
            return blockLight;
        }
    }
}