package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.server.ServerHandler;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class WhitelistCommands {

    private static final ServerHandler sh = Holiday.getInstance().getServerHandler();

    @Command(names = {"whitelist", "wl", "wl help", "whitelist help"}, perm = "holiday.whitelist", async = true)
    public static void onHelp(CommandSender sender) {

        String[] message = {
                "&cUsage: /wl info",
                "&cUsage: /wl <on/off>",
                "&cUsage: /wl rank <rank>",
                "&cUsage: /wl add <name>",
                "&cUsage: /wl remove <name>"
        };

        for (String s: message) {
            sender.sendMessage(CC.translate(s));
        }

    }


    @Command(names = {"whitelist", "wl"}, perm = "holiday.whitelist", async = true)
    public static void onToggleOn(CommandSender sender, @Param(name = "value") boolean bool) {
        sh.getThisServer().setWhitelisted(bool);
        sh.save();
        sender.sendMessage(CC.translate("&aSuccessfully toggled the whitelist " + (bool ? "on" : "off")));
    }


    @Command(names = {"whitelist rank", "wl rank"}, perm = "holiday.whitelist", async = true)
    public static void onRank(CommandSender sender, @Param(name = "rank") Rank rank) {
        sh.getThisServer().setWhitelistRank(rank);
        sh.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the whitelist rank to " + rank.getDisplayName()));
    }


    @Command(names = {"whitelist add", "wl add"}, perm = "holiday.whitelist", async = true)
    public static void onAdd(CommandSender sender, @Param(name = "player") OfflinePlayer player) {
        if (sh.getThisServer().getWhitelistedPlayers().contains(player.getUniqueId())) {
            sender.sendMessage(CC.translate("&cThis player is already whitelisted"));
        } else {
            sh.getThisServer().getWhitelistedPlayers().add(player.getUniqueId());
            sh.save();
            sender.sendMessage(CC.translate("&aSuccessfully added \"" + player.getName() + "\" &ato the whitelist"));
        }
    }

    @Command(names = {"whitelist remove", "wl remove"}, perm = "holiday.whitelist", async = true)
    public static void onremove(CommandSender sender, @Param(name = "player") OfflinePlayer player) {
        if (!sh.getThisServer().getWhitelistedPlayers().contains(player.getUniqueId())) {
            sender.sendMessage(CC.translate("&cThis player is not whitelisted"));
        } else {
            sh.getThisServer().getWhitelistedPlayers().remove(player.getUniqueId());
            sh.save();
            sender.sendMessage(CC.translate("&aSuccessfully removed \"" + player.getName() + "\" from the whitelist."));
        }
    }

    @Command(names = {"whitelist info", "wl info"}, perm = "holiday.whitelist", async = true)
    public static void onInfo(CommandSender sender) {
        sender.sendMessage(CC.CHAT_BAR);

        String status = sh.getThisServer().isWhitelisted() ? "&aOn" : "&cOff";
        sender.sendMessage(CC.PRIMARY + "Whitelist Info");
        sender.sendMessage(CC.CHAT + " Whitelist status: " + CC.translate(status));
        sender.sendMessage(CC.CHAT + " Whitelist rank: " + CC.translate(sh.getThisServer().getWhitelistRank().getDisplayName()));

        StringBuilder s = new StringBuilder();

        for (UUID uid : sh.getThisServer().getWhitelistedPlayers()) {
            Profile pr = Holiday.getInstance().getProfileHandler().getByUUIDFor5Minutes(uid);
            s.append(CC.CHAT).append(pr.getNameWithColor()).append(CC.CHAT).append(", ");
        }
        if (s.length() > 5)
        s = new StringBuilder(s.substring(0, s.length() - 4));
        s = new StringBuilder("&7[" + s + "&7]");
        if (s.toString().equalsIgnoreCase("&7[&7]")) {
            sender.sendMessage(CC.CHAT + " Whitelisted players: " + CC.PRIMARY + "None");
        } else {
            sender.sendMessage(CC.CHAT + " Whitelisted players: " + CC.translate(s.toString()));
        }

        sender.sendMessage(CC.CHAT_BAR);
    }



}
