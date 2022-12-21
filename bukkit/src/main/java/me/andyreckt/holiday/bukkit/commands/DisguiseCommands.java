package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.disguise.DisguiseMenu;
import me.andyreckt.holiday.bukkit.user.disguise.Disguise;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseCommands {

    private final Map<UUID, Cooldown> cooldownMap = new HashMap<>();

    @Command(names = {"disguise", "dis", "nick"}, permission = Perms.DISGUISE, description = "Disguise yourself as another player.")
    public void disguise(Player player) {
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.isDisguised()) {
            player.sendMessage(Locale.ALREADY_DISGUISED.getString());
            return;
        }

        if (cooldownMap.containsKey(player.getUniqueId())) {
            Cooldown oldCd = cooldownMap.get(player.getUniqueId());
            if (oldCd.hasExpired()) cooldownMap.remove(player.getUniqueId());
            else {
                player.sendMessage(Locale.COOLDOWN.getString().replace("%time%", TimeUtil.getDuration(oldCd.getRemaining())));
                return;
            }
        }

        cooldownMap.put(player.getUniqueId(), Cooldown.fromMinutes(3));
        new DisguiseMenu(new Disguise(player.getUniqueId())).openMenu(player);
    }

    @Command(names = {"undisguise", "undis", "unnick"}, permission = Perms.DISGUISE, description = "Undisguise yourself.")
    public void undisguise(Player player) {
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (!profile.isDisguised()) {
            player.sendMessage(Locale.NOT_DISGUISED.getString());
            return;
        }

        Holiday.getInstance().getDisguiseManager().unDisguise((Disguise) profile.getDisguise());
        player.sendMessage(Locale.DISGUISE_MESSAGE_OFF.getString());
    }

    //TODO: disguise list and manual disguise commands.

}
