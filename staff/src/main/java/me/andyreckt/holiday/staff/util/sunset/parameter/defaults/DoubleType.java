package me.andyreckt.holiday.staff.util.sunset.parameter.defaults;


import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DoubleType implements PType<Double> {

    @Override
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

    @Override
    public List<String> complete(Player sender,  String source) {
        return (new ArrayList<>());
    }

}