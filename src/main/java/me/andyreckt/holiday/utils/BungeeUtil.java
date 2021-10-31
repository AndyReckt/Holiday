package me.andyreckt.holiday.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.andyreckt.holiday.Holiday;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

/**
 * Created by Marko on 02.03.2019.
 */
public final class BungeeUtil {

	public static void sendMessage(Player source, String target, String message) {
		Validate.notNull(source, target, message, "Input values cannot be null!");

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Message");
			out.writeUTF(target);
			out.writeUTF(message);

			source.sendPluginMessage(Holiday.getInstance(), "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void sendToServer(Player player, String server) {
		Validate.notNull(player, server, "Input values cannot be null!");

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(server);

			player.sendPluginMessage(Holiday.getInstance(), "BungeeCord", out.toByteArray());
			player.sendMessage(CC.SECONDARY + "You're now being sent to " + CC.PRIMARY + server + CC.SECONDARY + '.');
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
