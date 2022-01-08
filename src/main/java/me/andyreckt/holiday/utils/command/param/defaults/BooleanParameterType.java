package me.andyreckt.holiday.utils.command.param.defaults;


import me.andyreckt.holiday.utils.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BooleanParameterType implements ParameterType<Boolean> {

    static final Map<String, Boolean> MAP = new HashMap<>();

    static {
        MAP.put("true", true);
        MAP.put("on", true);
        MAP.put("yes", true);

        MAP.put("false", false);
        MAP.put("off", false);
        MAP.put("no", false);
    }

    public Boolean transform(CommandSender sender, String source) {
        if (!MAP.containsKey(source.toLowerCase())) {
            sender.sendMessage("&cPlease use true or false");
            return (null);
        }

        return MAP.get(source.toLowerCase());
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return (MAP.keySet().stream().filter(string -> StringUtils.startsWithIgnoreCase(string, source)).collect(Collectors.toList()));
    }

}