package me.andyreckt.holiday.staff.util.sunset.parameter.custom;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.staff.util.sunset.parameter.PType;
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
            sender.sendMessage(Locale.NEED_NAME.getString());
        }

        Holiday plugin = Holiday.getInstance();

        if (sender instanceof Player && (source.equalsIgnoreCase("self"))) {
            return plugin.getApi().getProfile(((Player) sender).getUniqueId());
        }

        if (Bukkit.getPlayer(source) != null) {
            return plugin.getApi().getProfile(Bukkit.getPlayer(source).getUniqueId());
        }

        UUID cachedUUID = null;

        if (plugin.getDisguiseManager().isDisguised(source)) {
            cachedUUID = plugin.getDisguiseManager().getDisguise(source).getUuid();
        }

        if (plugin.getUuidCache().uuid(source.toLowerCase()) == null && cachedUUID == null) {
            sender.sendMessage(Locale.PLAYER_NOT_FOUND.getString());
            return (null);
        }

        cachedUUID = plugin.getUuidCache().uuid(source.toLowerCase());

        return plugin.getApi().getProfile(cachedUUID); //should work
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