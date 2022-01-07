package me.andyreckt.holiday.utils;

import me.andyreckt.holiday.database.redis.Redis;
import me.andyreckt.holiday.database.redis.packet.ClickablePacket;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Clickable {

	private final List<TextComponent> components = new ArrayList<>();

	public Clickable(String msg) {
		TextComponent message = new TextComponent(msg);

		this.components.add(message);
	}

	public Clickable(String msg, String hoverMsg, String clickString) {
		this.add(msg, hoverMsg, clickString);
	}

	public Clickable(String msg, String hoverMsg, ClickEvent.Action action, String clickString) {
		this.add(msg, hoverMsg, action, clickString);
	}

	public TextComponent add(String msg, String hoverMsg, String clickString) {
		TextComponent message = new TextComponent(CC.translate(msg));

		if (hoverMsg != null) {
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
		}

		if (clickString != null) {
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CC.translate(clickString)));
		}

		this.components.add(message);

		return message;
	}

	public TextComponent add(String msg, String hoverMsg, ClickEvent.Action action, String clickString) {
		TextComponent message = new TextComponent(CC.translate(msg));

		if (hoverMsg != null) {
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
		}

		if (clickString != null) {
			message.setClickEvent(new ClickEvent(action, CC.translate(clickString)));
		}

		this.components.add(message);

		return message;
	}

	public void add(String message) {
		this.components.add(new TextComponent(CC.translate(message)));
	}

	public void sendToPlayer(Player player) {
		player.sendMessage(this.asComponents());
	}

	public TextComponent[] asComponents() {
		return this.components.toArray(new TextComponent[0]);
	}


	public static void sendClickableToAll(String message, String hoverMessage, ClickEvent.Action clickAction, String clickCmd) {
		Redis.getPidgin().sendPacket(new ClickablePacket(message, hoverMessage, clickAction, clickCmd));
	}

	public static void sendClickableToAll(String message, String hoverMessage, String clickCmd) {
		Redis.getPidgin().sendPacket(new ClickablePacket(message, hoverMessage, ClickEvent.Action.RUN_COMMAND, clickCmd));
	}


}
