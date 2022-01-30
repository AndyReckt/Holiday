package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.ProfilePacket;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.grant.menu.GrantChooseRankMenu;
import me.andyreckt.holiday.player.grant.menu.GrantsMenu;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommands {

    @Command(names = "grants", perm = "holiday.grants.view")
    public static void grants(Player sender, @Param(name = "player")Profile target) {
        new GrantsMenu(target, false).openMenu(sender);
    }

    @Command(names = "grant", perm = "holiday.grants.edit")
    public static void grant(Player sender, @Param(name = "player") Profile target) {
        new GrantChooseRankMenu(target).openMenu(sender);
    }

    @Command(names = {"ogrant"},  perm = "op", async = true)
    public static void execute(CommandSender sender, @Param(name = "player") Profile target, @Param(name = "rank") Rank rank, @Param(name = "time") String time) {
        long tim = TimeUtil.getDuration(time);
        String ti = TimeUtil.getDuration(tim);

        Profile issuer = Holiday.getInstance().getProfileHandler().getByCommandSender(sender);

        Grant grant = new Grant(target.getUuid(), issuer.getUuid(), rank, tim);
        grant.save();


        sender.sendMessage(CC.translate("&aYou have granted the rank " + rank.getDisplayName() + " &ato " + target.getNameWithColor() + " &afor a duration of " + ti));
        Holiday.getInstance().getRedis().sendPacket(new ProfilePacket.ProfileMessagePacket(target, "&aYou have been granted " + rank.getDisplayName() + "&a for a duration of " + ti));
    }

}
