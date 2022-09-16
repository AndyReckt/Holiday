package me.andyreckt.holiday.utils.command.param.custom;


import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.sunset.parameter.PType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ProfileParameterType implements PType<Profile> {

    @Override
    public Profile transform(CommandSender sender, String source) {
        if (source.equals("")) {
            sender.sendMessage(CC.translate("&cYou need to enter a name"));
        }

        ProfileHandler ph = Holiday.getInstance().getProfileHandler();

        if (sender instanceof Player && (source.equalsIgnoreCase("self"))) {
            return ph.getByPlayer((Player) sender);
        }

        if (Bukkit.getPlayer(source) != null) {
            return ph.getByUUID(Bukkit.getPlayer(source).getUniqueId());
        }

        if (!ph.hasProfile(source)) {
            sender.sendMessage(CC.translate("&cThis player doesn't exist."));
            return (null);
        }

        return (ph.getByName(source)); //Seems to work
    }

    @Override
    public List<String> complete(Player sender, String source) {
        List<String> completions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                completions.add(player.getName());
            }
        }

        return (completions);
    }

}