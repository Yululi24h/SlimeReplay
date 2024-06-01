package me.koutachan.replay.replay.packet.in;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerSelfData extends ReplayWrapper<PlayerSelfData> {
    private Location pos;
    private int entityID;
    private UUID uuid;
    private int headYaw;
    private UserProfile userProfile;
    private ItemStack itemUse;
    //private List<PotionType> potionTypes = new ArrayList<>();
    private List<EntityData> entityData;

    public PlayerSelfData() {

    }

    @Override
    public void read() {
        super.read();
    }

    @Override
    public void write() {
        super.write();
    }

    @Override
    public boolean isSupportedVersion(ServerVersion version) {
        return true;
    }

    @Override
    public List<PacketWrapper<?>> getPacket() {
        return null;
    }

    @Override
    public List<PacketWrapper<?>> getUntilPacket() {
        return null;
    }
}
