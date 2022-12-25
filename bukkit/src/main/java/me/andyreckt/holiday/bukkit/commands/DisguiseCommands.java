package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.disguise.DisguiseMenu;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.disguise.Disguise;
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
        Disguise disguise = new Disguise(
                player.getUniqueId(),
                Holiday.getInstance().getDisguiseManager().getRandomName(),
                Holiday.getInstance().getDisguiseManager().getRandomSkin().getName(),
                Holiday.getInstance().getApi().getDefaultRank().getUuid());


        new DisguiseMenu(disguise).openMenu(player);
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

    @Command(names = {"manualdisguise", "mdis", "mnick"}, permission = Perms.DISGUISE_MANUAL, description = "Disguise yourself as another player.")
    public void manualDisguise(Player player, @Param(name = "name") String name, @Param(name = "skin") String skin, @Param(name = "rank", baseValue = "default") IRank rank) {
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.isDisguised()) {
            player.sendMessage(Locale.ALREADY_DISGUISED.getString());
            return;
        }

        Disguise disguise = new Disguise(
                player.getUniqueId(),
                name,
                skin,
                rank.getUuid());

        Holiday.getInstance().getDisguiseManager().disguise(disguise, true);
        player.sendMessage(Locale.DISGUISE_MESSAGE.getString()
                .replace("%skin%", skin)
                .replace("%name%", UserConstants.getDisplayNameWithColor(
                        Holiday.getInstance().getApi().getProfile(player.getUniqueId()))));
    }

    @Command(names = {"disguiselist", "dislist", "nicklist"}, permission = Perms.DISGUISE_LIST, description = "List all disguises.")
    public void disguiseList(Player player) {
        if (Holiday.getInstance().getDisguiseManager().getDisguises().isEmpty()) {
            player.sendMessage(Locale.NOBODY_DISGUISED.getString());
            return;
        }

        player.sendMessage(CC.CHAT_BAR);
        Holiday.getInstance().getDisguiseManager().getDisguises().values().forEach(disguise -> {
            Profile profile = Holiday.getInstance().getApi().getProfile(disguise.getUuid());
            String originalName = UserConstants.getNameWithColor(profile);
            String name = UserConstants.getDisplayNameWithColor(profile);
            player.sendMessage(Locale.DISGUISE_LIST.getString()
                    .replace("%name%", name)
                    .replace("%skin%", disguise.getSkin().getName())
                    .replace("%originalName%", originalName));
        });
        player.sendMessage(CC.CHAT_BAR);
    }

}
