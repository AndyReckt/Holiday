package me.andyreckt.holiday.utils.command.param.defaults;

import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerParameterType implements ParameterType<Player> {

    public Player transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender);
        }
        if (!(sender instanceof Player) && (source.equalsIgnoreCase("self") || source.equals(""))) {
            sender.sendMessage("§cAre you insane?");
            return (null);
        }

        Player player = Bukkit.getPlayer(source);

        if (player == null) {
            sender.sendMessage(CC.translate("&cThis player does not exist."));
            return (null);
        }

        return (player);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source) && (sender.canSee(player))) {
                completions.add(player.getName());
            }
        }

        return (completions);
    }

}