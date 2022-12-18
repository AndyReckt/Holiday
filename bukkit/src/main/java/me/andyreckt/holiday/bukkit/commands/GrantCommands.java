package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.bukkit.server.menu.grant.*;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GrantCommands {

    @Command(names = "grants", permission = Perms.GRANTS_VIEW)
    public void grants(Player sender, @Param(name = "player") Profile target) {
        new GrantsMenu(target).openMenu(sender);
    }

    @Command(names = "grant", permission = Perms.GRANTS_EDIT)
    public void grant(Player sender, @Param(name = "player") Profile target) {
        new GrantChooseRankMenu(target).openMenu(sender);
    }

    @Command(names = {"ogrant"}, async = true)
    public void execute(ConsoleCommandSender sender, @Param(name = "player") Profile target, @Param(name = "rank") IRank rank, @Param(name = "reason") String reason, @Param(name = "time") String time) {
        long tim = TimeUtil.getDuration(time);
        String ti = TimeUtil.getDuration(tim);

        Profile issuer = sender instanceof Player ? Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId()) : UserProfile.getConsoleProfile();
        Grant grant = new Grant(target.getUuid(), rank, issuer.getUuid(), reason,"$undefined", tim);
        Holiday.getInstance().getApi().saveGrant(grant);

        String str = Locale.GRANT_PLAYER.getString()
                .replace("%player%", Holiday.getInstance().getNameWithColor(target))
                .replace("%rank%", rank.getDisplayName())
                .replace("%duration%", ti);
        sender.sendMessage(CC.translate(str));
    }

}
