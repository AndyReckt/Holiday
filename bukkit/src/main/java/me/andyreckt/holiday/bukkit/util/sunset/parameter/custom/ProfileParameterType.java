package me.andyreckt.holiday.bukkit.util.sunset.parameter.custom;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.sunset.parameter.PType;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.http.UUIDFetcher;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ProfileParameterType implements PType<Profile> {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
    private static final String UUID_REGEX_NO_HYPHENS = "[0-9a-fA-F]{32}";

    @Override
    public Profile transform(CommandSender sender, String source) {

        if (source.equals("")) {
            sender.sendMessage(Locale.NEED_NAME.getString());
        }

        Holiday plugin = Holiday.getInstance();

        if (source.matches(UUID_REGEX) || source.matches(UUID_REGEX_NO_HYPHENS)) {
            UUID uuid = UUID.fromString(source);
            return ((HolidayAPI) plugin.getApi()).getUserManager().getProfileNoCreate(uuid);
        }

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

        if (cachedUUID == null && plugin.getUuidCache().uuid(source.toLowerCase()) == null) {
            if (!Locale.SERVER_CREATE_PROFILE_IF_NOT_EXISTS.getBoolean()) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.getString());
                return (null);
            }

            UUID fetchedUUID = UUIDFetcher.getSync(source);

            if (fetchedUUID == null) {
                sender.sendMessage(Locale.PLAYER_NOT_FOUND.getString());
                return (null);
            }

            Profile profile = plugin.getApi().getProfile(fetchedUUID);
            profile.setName(source.toLowerCase());
            plugin.getApi().saveProfile(profile);
            return plugin.getApi().getProfile(fetchedUUID);
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