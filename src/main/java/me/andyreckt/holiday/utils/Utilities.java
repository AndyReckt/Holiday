package me.andyreckt.holiday.utils;

import com.google.common.collect.ImmutableSet;
import me.andyreckt.holiday.Holiday;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Utilities {
    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
    }

    public static void playSound(Player player, Sound sound) {
         player.playSound(player.getLocation(), sound, 2.0F, 2.0F);
    }
    public static void playSound(Player player, String sound) {
         try {
           player.playSound(player.getLocation(), Sound.valueOf(sound), 2.0F, 2.0F);
         } catch (Exception ignored) {}

    }
    public static int getPing(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return entityPlayer.ping;
    }
    public static void sendToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(Holiday.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (ImmutableSet.copyOf(classes));
    }



    public static List<EntityPlayer> getEntityPlayers(List<Player> players) {
        return players.stream().filter(Objects::nonNull).map(player -> ((CraftPlayer) player).getHandle()).collect(Collectors.toList());
    }

    public static int niceLocation(double pos){
        return MathHelper.floor(pos * 32.0D);
    }

    public static byte niceRotation(float yawpitch){
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    public static String serializeLocation(Location l) {
        String s = "";
        s = s + "@w;" + l.getWorld().getName();
        s = s + ":@x;" + l.getX();
        s = s + ":@y;" + l.getY();
        s = s + ":@z;" + l.getZ();
        s = s + ":@p;" + l.getPitch();
        s = s + ":@ya;" + l.getYaw();
        return s;
    }

    public static Location deserializeLocation(String s) {
        String[] attributes = s.split(":");

        World world = null;
        Double x = null;
        Double y = null;
        Double z = null;
        Float pitch = null;
        Float yaw = null;

        for (String attribute : attributes) {
            String[] split = attribute.split(";");

            if (split[0].equalsIgnoreCase("@w")) {
                world = Bukkit.getWorld(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("@x")) {
                x = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("@y")) {
                y = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("@z")) {
                z = Double.parseDouble(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("@p")) {
                pitch = Float.parseFloat(split[1]);
                continue;
            }

            if (split[0].equalsIgnoreCase("@ya")) {
                yaw = Float.parseFloat(split[1]);
            }
        }

        if (world == null || x == null || y == null || z == null || pitch == null || yaw == null) {
            return null;
        }

        return new Location(world, x, y, z, yaw, pitch);
    }



}