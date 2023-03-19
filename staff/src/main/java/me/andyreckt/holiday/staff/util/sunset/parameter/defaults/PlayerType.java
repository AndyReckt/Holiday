package me.andyreckt.holiday.staff.util.sunset.parameter.defaults;


import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerType implements PType<Player> {
    @Override
    public Player transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender);
        }
        if (!(sender instanceof Player) && (source.equalsIgnoreCase("self") || source.equals(""))) {
            sender.sendMessage(ChatColor.RED + "Are you insane?");
            return (null);
        }

        Player player = Bukkit.getPlayer(source);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "This player does not exist.");
            return (null);
        }

        return (player);
    }
    @Override
    public List<String> complete(Player sender,  String source) {
        List<String> completions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source) && (sender.canSee(player))) {
                completions.add(player.getName());
            }
        }

        return (completions);
    }

}