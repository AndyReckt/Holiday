package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.PlayerMessagePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.bukkit.server.menu.grant.*;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
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
    public void ogrant(ConsoleCommandSender sender,
                        @Param(name = "player") Profile target,
                        @Param(name = "rank") IRank rank,
                        @Param(name = "time") String time,
                        @Param(name = "reason", wildcard = true) String reason) {
        long tim = TimeUtil.getDuration(time);
        String ti = TimeUtil.getDuration(tim);
        Holiday plugin = Holiday.getInstance();

        Profile issuer = UserProfile.getConsoleProfile();
        Grant grant = new Grant(target.getUuid(), rank, issuer.getUuid(), reason,plugin.getThisServer().getServerName(), tim);
        plugin.getApi().saveGrant(grant);

        String str = Locale.GRANT_PLAYER.getString()
                .replace("%player%", UserConstants.getNameWithColor(target))
                .replace("%rank%", rank.getDisplayName())
                .replace("%duration%", ti);
        sender.sendMessage(CC.translate(str));
        String str2 = Locale.GRANT_TARGET.getString()
                .replace("%rank%", rank.getDisplayName())
                .replace("%duration%", String.valueOf(TimeUtil.getDuration(ti)))
                .replace("%reason%", reason);
        PacketHandler.send(new PlayerMessagePacket(target.getUuid(), str2));
    }

    @Command(names = "rgrant", async = true)
    public void rgrant(ConsoleCommandSender sender,
                        @Param(name = "player") Profile target,
                        @Param(name = "rank") IRank rank,
                        @Param(name = "reason", wildcard = true) String reason) {
        Profile issuer = UserProfile.getConsoleProfile();
        target.getActiveGrants().stream()
                .filter(grant -> grant.getRank().equals(rank))
                .forEach(grant -> {
                    grant.revoke(issuer.getUuid(), reason, Holiday.getInstance().getThisServer().getServerName());
                    Holiday.getInstance().getApi().saveGrant(grant);
                });
    }

}
