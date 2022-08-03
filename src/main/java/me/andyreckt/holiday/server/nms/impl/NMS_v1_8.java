package me.andyreckt.holiday.server.nms.impl;

import com.mojang.authlib.GameProfile;
import me.andyreckt.holiday.server.nms.INMS;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;


public class NMS_v1_8 implements INMS {

    @Override
    public void removeExecute(final Player player) {
        final PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void addExecute(final Player player) {
        final PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle());
        MinecraftServer.getServer().getPlayerList().sendAll(packetPlayOutPlayerInfo);
    }

    @Override
    public void respawnPlayer(Player player) {
        Location previousLocation = player.getLocation().clone();
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        entityPlayer.playerConnection.sendPacket(
                new PacketPlayOutRespawn(entityPlayer.getWorld().worldProvider.getDimension(),
                        entityPlayer.getWorld().worldData.getDifficulty(),
                        entityPlayer.getWorld().worldData.getType(),
                        WorldSettings.EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name())));
        player.teleport(previousLocation);
    }

    @Override
    public void updatePlayer(final Player player) {
        player.updateInventory();
        player.setGameMode(player.getGameMode());

        PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(inventory.getHeldItemSlot());

        double oldHealth = player.getHealth();

        int oldFood = player.getFoodLevel();
        float oldSat = player.getSaturation();
        player.setFoodLevel(20);
        player.setFoodLevel(oldFood);
        player.setSaturation(5.0F);
        player.setSaturation(oldSat);

        player.setMaxHealth(player.getMaxHealth());

        player.setHealth(20.0F);
        player.setHealth(oldHealth);

        float experience = player.getExp();
        int totalExperience = player.getTotalExperience();
        player.setExp(experience);
        player.setTotalExperience(totalExperience);

        player.setWalkSpeed(player.getWalkSpeed());

    }

    @Override
    public void clearDataWatcher(Player player) {
        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
    }

    @Override
    public double[] recentTps() {
        return MinecraftServer.getServer().recentTps;
    }
}
