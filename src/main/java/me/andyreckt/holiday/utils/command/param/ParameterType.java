package me.andyreckt.holiday.utils.command.param;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface ParameterType<T> {

    T transform(CommandSender sender, String source);

    List<String> tabComplete(Player sender, Set<String> flags, String source);

}