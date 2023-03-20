package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.PlayerMessagePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;


import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.bukkit.server.menu.grant.*;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GrantCommands extends BaseCommand {

    @CommandPermission("core.command.grants.view")
    @CommandAlias("grants")
    @CommandCompletion("@players")
    @Conditions("player")
    public void grants(CommandSender sender, @Name("target") @Single Profile target) {
        new GrantsMenu(target).openMenu((Player) sender);
    }

    @CommandPermission("core.command.grants.edit")
    @CommandAlias("grant")
    @CommandCompletion("@players")
    @Conditions("player")
    public void grant(CommandSender sender, @Name("target") @Single Profile target) {
        new GrantChooseRankMenu(target).openMenu((Player) sender);
    }

    @Conditions("console")
    @CommandAlias("ogrant")
    public void ogrant(CommandSender sender,
                       @Name("target") @Single Profile target,
                       @Single @Name("rank") IRank rank,
                       @Single @Name("duration") Duration duration,
                       @Name("reason") String reason) {
        Holiday plugin = Holiday.getInstance();

        Profile issuer = UserProfile.getConsoleProfile();
        Grant grant = new Grant(target.getUuid(), rank, issuer.getUuid(), reason,plugin.getThisServer().getServerName(), duration);
        plugin.getApi().saveGrant(grant);

        String str = Locale.GRANT_PLAYER.getString()
                .replace("%player%", UserConstants.getNameWithColor(target))
                .replace("%rank%", rank.getDisplayName())
                .replace("%duration%", duration.toRoundedTime());
        sender.sendMessage(CC.translate(str));
        String str2 = Locale.GRANT_TARGET.getString()
                .replace("%rank%", rank.getDisplayName())
                .replace("%duration%", duration.toRoundedTime())
                .replace("%reason%", reason);
        PacketHandler.send(new PlayerMessagePacket(target.getUuid(), str2));
    }

    @Conditions("console")
    @CommandAlias("rgrant")
    public void rgrant(CommandSender sender,
                        @Name("target") @Single Profile target,
                        @Single @Name("rank") IRank rank,
                        @Name("reason") String reason) {
        Profile issuer = UserProfile.getConsoleProfile();
        target.getActiveGrants().stream()
                .filter(grant -> grant.getRank().equals(rank))
                .forEach(grant -> {
                    grant.revoke(issuer.getUuid(), reason, Holiday.getInstance().getThisServer().getServerName());
                    Holiday.getInstance().getApi().saveGrant(grant);
                });
    }

}
