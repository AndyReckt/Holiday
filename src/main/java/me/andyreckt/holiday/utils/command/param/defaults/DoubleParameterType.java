package me.andyreckt.holiday.utils.command.param.defaults;

import me.andyreckt.holiday.utils.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DoubleParameterType implements ParameterType<Double> {

    public Double transform(CommandSender sender, String source) {
        if (source.toLowerCase().contains("e")) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
            return (null);
        }

        try {
            double parsed = Double.parseDouble(source);

            if (Double.isNaN(parsed) || !Double.isFinite(parsed)) {
                sender.sendMessage("§cThis is not a valid number");
                return (null);
            }

            return (parsed);
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cThis is not a valid number");
            return (null);
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return (new ArrayList<>());
    }

}