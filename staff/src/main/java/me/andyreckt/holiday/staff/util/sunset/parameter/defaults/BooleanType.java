package me.andyreckt.holiday.staff.util.sunset.parameter.defaults;



import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BooleanType implements PType<Boolean> {

    static final Map<String, Boolean> MAP = new HashMap<>();

    static {
        MAP.put("true", true);
        MAP.put("on", true);
        MAP.put("yes", true);

        MAP.put("false", false);
        MAP.put("off", false);
        MAP.put("no", false);
    }

    @Override
    public Boolean transform(CommandSender sender, String source) {
        if (!MAP.containsKey(source.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "Please use true or false");
            return (null);
        }

        return MAP.get(source.toLowerCase());
    }

    @Override
    public List<String> complete(Player sender,  String source) {
        return (MAP.keySet().stream().filter(string -> StringUtils.startsWithIgnoreCase(string, source)).collect(Collectors.toList()));
    }

}