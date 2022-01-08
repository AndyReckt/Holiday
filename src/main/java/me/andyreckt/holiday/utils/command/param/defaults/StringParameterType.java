package me.andyreckt.holiday.utils.command.param.defaults;


import me.andyreckt.holiday.utils.command.param.ParameterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringParameterType implements ParameterType<String> {

    public String transform(CommandSender sender, String source) {
        return source;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return (new ArrayList<>());
    }

}