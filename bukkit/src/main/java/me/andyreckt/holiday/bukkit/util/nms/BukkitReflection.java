package me.andyreckt.holiday.bukkit.util.nms;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BukkitReflection
{
    private static final String CRAFT_BUKKIT_PACKAGE;
    private static final String NET_MINECRAFT_SERVER_PACKAGE;
    private static final Class CRAFT_SERVER_CLASS;
    private static final Method CRAFT_SERVER_GET_HANDLE_METHOD;
    private static final Class PLAYER_LIST_CLASS;
    private static final Field PLAYER_LIST_MAX_PLAYERS_FIELD;
    private static final Class CRAFT_PLAYER_CLASS;
    private static final Method CRAFT_PLAYER_GET_HANDLE_METHOD;
    private static final Class ENTITY_PLAYER_CLASS;
    private static final Field ENTITY_PLAYER_PING_FIELD;
    private static final Class CRAFT_ITEM_STACK_CLASS;
    private static final Method CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD;
    private static final Class ENTITY_ITEM_STACK_CLASS;
    private static final Method ENTITY_ITEM_STACK_GET_NAME;
    private static final Class SPIGOT_CONFIG_CLASS;
    private static final Field SPIGOT_CONFIG_BUNGEE_FIELD;
    
    public static int getPing(final Player player) {
        try {
            final int ping = BukkitReflection.ENTITY_PLAYER_PING_FIELD.getInt(BukkitReflection.CRAFT_PLAYER_GET_HANDLE_METHOD.invoke(player));
            return Math.max(ping, 0);
        }
        catch (Exception e) {
            return 1;
        }
    }
    
    public static void setMaxPlayers(final Server server, final int slots) {
        try {
            BukkitReflection.PLAYER_LIST_MAX_PLAYERS_FIELD.set(BukkitReflection.CRAFT_SERVER_GET_HANDLE_METHOD.invoke(server), slots);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getItemStackName(final ItemStack itemStack) {
        try {
            return (String)BukkitReflection.ENTITY_ITEM_STACK_GET_NAME.invoke(BukkitReflection.CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD.invoke(itemStack, itemStack), new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    static {
        try {
            final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + version + ".";
            NET_MINECRAFT_SERVER_PACKAGE = "net.minecraft.server." + version + ".";
            CRAFT_SERVER_CLASS = Class.forName(BukkitReflection.CRAFT_BUKKIT_PACKAGE + "CraftServer");
            (CRAFT_SERVER_GET_HANDLE_METHOD = BukkitReflection.CRAFT_SERVER_CLASS.getDeclaredMethod("getHandle")).setAccessible(true);
            PLAYER_LIST_CLASS = Class.forName(BukkitReflection.NET_MINECRAFT_SERVER_PACKAGE + "PlayerList");
            (PLAYER_LIST_MAX_PLAYERS_FIELD = BukkitReflection.PLAYER_LIST_CLASS.getDeclaredField("maxPlayers")).setAccessible(true);
            CRAFT_PLAYER_CLASS = Class.forName(BukkitReflection.CRAFT_BUKKIT_PACKAGE + "entity.CraftPlayer");
            (CRAFT_PLAYER_GET_HANDLE_METHOD = BukkitReflection.CRAFT_PLAYER_CLASS.getDeclaredMethod("getHandle")).setAccessible(true);
            ENTITY_PLAYER_CLASS = Class.forName(BukkitReflection.NET_MINECRAFT_SERVER_PACKAGE + "EntityPlayer");
            (ENTITY_PLAYER_PING_FIELD = BukkitReflection.ENTITY_PLAYER_CLASS.getDeclaredField("ping")).setAccessible(true);
            CRAFT_ITEM_STACK_CLASS = Class.forName(BukkitReflection.CRAFT_BUKKIT_PACKAGE + "inventory.CraftItemStack");
            (CRAFT_ITEM_STACK_AS_NMS_COPY_METHOD = BukkitReflection.CRAFT_ITEM_STACK_CLASS.getDeclaredMethod("asNMSCopy", ItemStack.class)).setAccessible(true);
            ENTITY_ITEM_STACK_CLASS = Class.forName(BukkitReflection.NET_MINECRAFT_SERVER_PACKAGE + "ItemStack");
            ENTITY_ITEM_STACK_GET_NAME = BukkitReflection.ENTITY_ITEM_STACK_CLASS.getDeclaredMethod("getName");
            SPIGOT_CONFIG_CLASS = Class.forName("org.spigotmc.SpigotConfig");
            (SPIGOT_CONFIG_BUNGEE_FIELD = BukkitReflection.SPIGOT_CONFIG_CLASS.getDeclaredField("bungee")).setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Bukkit/NMS Reflection");
        }
    }
}
