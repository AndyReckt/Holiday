package me.andyreckt.holiday.server.nms.impl;

import com.mojang.authlib.GameProfile;
import me.andyreckt.holiday.server.nms.INMS;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMS_v1_7 implements INMS {
    @Override
    public void removeExecute(final Player player) {
        MinecraftServer.getServer().getPlayerList().sendAll(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)player).getHandle()));
    }

    @Override
    public void addExecute(final Player player) {
        MinecraftServer.getServer().getPlayerList().sendAll(PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer)player).getHandle()));
    }

    @Override
    public void respawnPlayer(Player player) {

    }

    @Override
    public void updatePlayer(final Player player) {
        final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        final Location previousLocation = player.getLocation().clone();
        PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
        PacketPlayOutPlayerInfo.addPlayer(entityPlayer);
        entityPlayer.playerConnection.sendPacket((new PacketPlayOutRespawn(0, entityPlayer.getWorld().difficulty, entityPlayer.getWorld().worldData.getType(), EnumGamemode.valueOf(entityPlayer.getBukkitEntity().getGameMode().name()))));
        player.getInventory().setItemInHand(player.getItemInHand());
        player.updateInventory();
        player.teleport(previousLocation);
    }

    @Override
    public void updateCache(GameProfile profile) {
        //TODO make it useable
    }

    @Override
    public GameProfile getGameProfile(Player player) {
        return null;
    }
}
