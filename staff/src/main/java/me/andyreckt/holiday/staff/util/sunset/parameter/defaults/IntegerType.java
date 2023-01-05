package me.andyreckt.holiday.staff.util.sunset.parameter.defaults;

import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IntegerType implements PType<Integer> {

    @Override
    public Integer transform(CommandSender sender, String source) {
        try {
            return (Integer.parseInt(source));
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
            return (null);
        }
    }

    @Override
    public List<String> complete(Player sender, String string) {
        return new ArrayList<>();
    }
}
