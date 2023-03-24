package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.disguise.DisguiseMenu;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.menu.anvilgui.AnvilGUI;
  
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
 
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class DisguiseCommands extends BaseCommand {

    private final Map<UUID, Cooldown> cooldownMap = new HashMap<>();

    @CommandAlias("disguise|dis|nick")
    @CommandPermission("core.command.disguise")
    @Description("Disguise yourself as another player.")
    @Conditions("player")
    public void disguise(CommandSender sender) {
        Player player = (Player) sender;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.isDisguised()) {
            player.sendMessage(Locale.ALREADY_DISGUISED.getString());
            return;
        }

        if (cooldownMap.containsKey(player.getUniqueId())) {
            Cooldown oldCd = cooldownMap.get(player.getUniqueId());
            if (oldCd.hasExpired()) cooldownMap.remove(player.getUniqueId());
            else {
                player.sendMessage(Locale.COOLDOWN.getString().replace("%time%", Duration.of(oldCd.getRemaining()).toRoundedTime()));
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

    @CommandAlias("undisguise|undis|unnick")
    @CommandPermission("core.command.disguise")
    @Description("Undisguise yourself.")
    @Conditions("player")
    public void undisguise(CommandSender sender) {
        Player player = (Player) sender;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (!profile.isDisguised()) {
            player.sendMessage(Locale.NOT_DISGUISED.getString());
            return;
        }

        Holiday.getInstance().getDisguiseManager().unDisguise((Disguise) profile.getDisguise());
        player.sendMessage(Locale.DISGUISE_MESSAGE_OFF.getString());
    }

    @CommandAlias("manualdisguise|mdis|mnick")
    @CommandPermission("core.command.disguise.manual")
    @Description("Disguise yourself as another player.")
    @CommandCompletion("@dnames @dskins @ranks")
    @Conditions("player")
    public void manualDisguise(CommandSender sender,
                               @Single @Name("name") String name,
                               @Single @Name("skin") String skin,
                               @Optional @Single @Name("rank") @Default("default") IRank rank) {
        Player player = (Player) sender;
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (profile.isDisguised()) {
            player.sendMessage(Locale.ALREADY_DISGUISED.getString());
            return;
        }

        if (!Pattern.matches(UserConstants.DISGUISE_NAME_MATCHER, name)) {
            player.sendMessage(Locale.INVALID_NAME.getString());
            return;
        }

        if (!Pattern.matches(UserConstants.DISGUISE_NAME_MATCHER, name)) {
            player.sendMessage(Locale.INVALID_NAME.getString());
            return;
        }

        if (!Holiday.getInstance().getDisguiseManager().isPlayerOnline(name)) {
            player.sendMessage(Locale.DISGUISE_NAME_TAKEN.getString());
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

    @CommandAlias("disguiselist|dislist|nicklist")
    @CommandPermission("core.command.disguise.list")
    @Description("List all disguises.")
    @Conditions("player")
    public void disguiseList(CommandSender player) {
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
