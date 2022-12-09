package me.andyreckt.holiday.bukkit.util.sunset.parameter.custom;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.PType;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserManager;
import me.andyreckt.holiday.core.user.UserProfile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProfileParameterType implements PType<Profile> {

    @Override
    public Profile transform(CommandSender sender, String source) {

        if (source.equals("")) {
            sender.sendMessage(CC.translate("&cYou need to enter a name"));
        }

        if (sender instanceof Player && (source.equalsIgnoreCase("self"))) {
            return Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId());
        }

        if (Bukkit.getPlayer(source) != null) {
            return Holiday.getInstance().getApi().getProfile(Bukkit.getPlayer(source).getUniqueId());
        }

        if (Holiday.getInstance().getUuidCache().uuid(source.toLowerCase()) == null) {
            sender.sendMessage(CC.translate("&cThis player doesn't exist."));
            return (null);
        }

        UUID cachedUUID = Holiday.getInstance().getUuidCache().uuid(source.toLowerCase());

        return Holiday.getInstance().getApi().getProfile(cachedUUID); //should work
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