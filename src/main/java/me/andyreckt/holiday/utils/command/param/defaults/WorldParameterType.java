package me.andyreckt.holiday.utils.command.param.defaults;

import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldParameterType implements ParameterType<World> {

    public World transform(CommandSender sender, String source) {
        World world = Bukkit.getWorld(source);

        if (world == null) {
            sender.sendMessage(CC.translate("&cThis world doesn't exist"));
            return (null);
        }

        return (world);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            if (StringUtils.startsWithIgnoreCase(world.getName(), source)) {
                completions.add(world.getName());
            }
        }

        return (completions);
    }

}