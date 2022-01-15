package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.player.rank.menu.RankManageMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RankCommands {

    private static final RankHandler rh = Holiday.getInstance().getRankHandler();

    @Command(names = {"rank create"}, perm = "op", async = true)
    public static void create(CommandSender sender, @Param(name = "rank") String string) {

        if (rh.getFromName(string) != null) {
            sender.sendMessage(CC.translate("&cThe rank " + rh.getFromName(string).getDisplayName() + " already exists."));
            return;
        }

        Rank rank = rh.createRank(string);
        rank.save();

    }

    @Command(names = {"rank manage", "rank edit"}, perm = "op")
    public static void manage(Player sender, @Param(name = "rank") Rank rank) {
        new RankManageMenu(rank).openMenu(sender);
    }

}
