package me.andyreckt.holiday.staff.util.sunset.parameter.defaults;


import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StringType implements PType<String> {

    public String transform(CommandSender sender, String source) {
        return source;
    }

    public List<String> complete(Player sender,  String source) {
        return (new ArrayList<>());
    }

}