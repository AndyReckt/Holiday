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
import java.util.concurrent.CompletableFuture;

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

    public static CompletableFuture<Boolean> hasVotedOnNameMC(UUID uuid) {
        String server = Locale.NETWORK_IP.getString();

        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.namemc.com/server/" + server + "/likes?profile=" + uuid.toString());
                Scanner scanner = new Scanner(url.openStream());

                return Boolean.parseBoolean(scanner.next());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        });
    }

}
