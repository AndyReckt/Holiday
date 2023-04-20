package me.andyreckt.holiday.bukkit.util.text;

import lombok.experimental.UtilityClass;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;

@UtilityClass
public final class CC {

	public static String PRIMARY;
	public static String SECONDARY;
	public static String CHAT;

	public static ChatColor PRIMARY_CC;
	public static ChatColor SECONDARY_CC;
	public static ChatColor CHAT_CC;

	public static String B_PRIMARY;
	public static String B_SECONDARY;

	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String OBFUSCATED = MAGIC;
	public static final String B = BOLD;
	public static final String M = MAGIC;
	public static final String O = MAGIC;
	public static final String I = ITALIC;
	public static final String S = STRIKE_THROUGH;
	public static final String R = RESET;
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String D_BLUE = DARK_BLUE;
	public static final String D_AQUA = DARK_AQUA;
	public static final String D_GRAY = DARK_GRAY;
	public static final String D_GREEN = DARK_GREEN;
	public static final String D_PURPLE = DARK_PURPLE;
	public static final String D_RED = DARK_RED;
	public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
	public static final String L_PURPLE = LIGHT_PURPLE;
	public static final String PINK = L_PURPLE;
	public static final String B_BLUE = BLUE + B;
	public static final String B_AQUA = AQUA + B;
	public static final String B_YELLOW = YELLOW + B;
	public static final String B_RED = RED + B;
	public static final String B_GRAY = GRAY + B;
	public static final String B_GOLD = GOLD + B;
	public static final String B_GREEN = GREEN + B;
	public static final String B_WHITE = WHITE + B;
	public static final String B_BLACK = BLACK + B;
	public static final String BD_BLUE = D_BLUE + B;
	public static final String BD_AQUA = D_AQUA + B;
	public static final String BD_GRAY = D_GRAY + B;
	public static final String BD_GREEN = D_GREEN + B;
	public static final String BD_PURPLE = D_PURPLE + B;
	public static final String BD_RED = D_RED + B;
	public static final String BL_PURPLE = L_PURPLE + B;
	public static final String I_BLUE = BLUE + I;
	public static final String I_AQUA = AQUA + I;
	public static final String I_YELLOW = YELLOW + I;
	public static final String I_RED = RED + I;
	public static final String I_GRAY = GRAY + I;
	public static final String I_GOLD = GOLD + I;
	public static final String I_GREEN = GREEN + I;
	public static final String I_WHITE = WHITE + I;
	public static final String I_BLACK = BLACK + I;
	public static final String ID_RED = D_RED + I;
	public static final String ID_BLUE = D_BLUE + I;
	public static final String ID_AQUA = D_AQUA + I;
	public static final String ID_GRAY = D_GRAY + I;
	public static final String ID_GREEN = D_GREEN + I;
	public static final String ID_PURPLE = D_PURPLE + I;
	public static final String IL_PURPLE = L_PURPLE + I;
	public static final String VAPE = "§8 §8 §1 §3 §3 §7 §8 §r";
	public static final String BLANK_LINE = VAPE;
	public static final String BL = BLANK_LINE;
	public static final String UNICODE_VERTICAL_BAR = CC.GRAY + StringEscapeUtils.unescapeJava("┃");
	public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("⚠");
	public static final String UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("◀");
	public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("▶");
	public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("«");
	public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("»");
	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("❤");
	public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
	public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
	public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------";

	public static final char NICE_CHAR = '●';
	public static final char HEART = '❤';
	public static final String LINE = "▏";
	public static final String LINE_2 = "▎";
	public static final String LINE_3 = "▍";
	public static final String LINE_4 = "▌";
	public static final String LINE_5 = "▋";
	public static final String LINE_6 = "▊";
	public static final String LINE_7 = "▉";
	public static final String LINE_8 = "█";

	public static String translate(String input) {
		return ChatColor.translateAlternateColorCodes('&', input).replace("\n", "\n");
	}

	public static List<String> translate(List<String> input) {
		return input.stream().map(CC::translate).collect(Collectors.toList());
	}

	public static ChatColor getColor(String text) {
		return ChatColor.valueOf(text.toUpperCase());
	}

	public static void setupColors() {
		PRIMARY_CC = getColor(Locale.COLOR_PRIMARY.getString());
		SECONDARY_CC = getColor(Locale.COLOR_SECONDARY.getString());
		CHAT_CC = getColor(Locale.COLOR_CHAT.getString());

		PRIMARY = getColor(Locale.COLOR_PRIMARY.getString()).toString();
		SECONDARY = getColor(Locale.COLOR_SECONDARY.getString()).toString();
		CHAT = getColor(Locale.COLOR_CHAT.getString()).toString();

		B_PRIMARY = PRIMARY + ChatColor.BOLD;
		B_SECONDARY = SECONDARY + ChatColor.BOLD;
	}

	public static String addNetworkPlaceholder(String string) {

		string = string.replace("%network_name%", Locale.NETWORK_NAME.getString());
		string = string.replace("%network_ip%", Locale.NETWORK_IP.getString());
		string = string.replace("%discord%", Locale.NETWORK_DISCORD.getString());
		string = string.replace("%teamspeak%", Locale.NETWORK_TEAMSPEAK.getString());
		string = string.replace("%website%", Locale.NETWORK_WEBSITE.getString());
		string = string.replace("%servername%", Locale.SERVER_NAME.getString());
		string = string.replace("%store%", Locale.NETWORK_STORE.getString());
		string = string.replace("%twitter%", Locale.NETWORK_TWITTER.getString());

		return string;
	}

	public static String formatTps(double tps) {
		if (tps >= 18.0) {
			return CC.GREEN + String.format("%.2f", tps);
		} else if (tps >= 16.0) {
			return CC.YELLOW + String.format("%.2f", tps);
		} else {
			return CC.RED + String.format("%.2f", tps);
		}
	}

}
