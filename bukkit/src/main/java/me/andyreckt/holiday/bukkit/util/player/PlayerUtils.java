package me.andyreckt.holiday.bukkit.util.player;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.nms.BukkitReflection;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class PlayerUtils {

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

    public static int getPing(Player player) {
        return BukkitReflection.getPing(player);
    }

    public static boolean hasVotedOnNameMC(UUID uuid) {
        try (Scanner scanner = new Scanner(
                new URL("https://api.namemc.com/server/" + Locale.NETWORK_IP.getString() + "/likes?profile=" + uuid.toString())
                        .openStream()).useDelimiter("\\A")) {
            return Boolean.parseBoolean(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
