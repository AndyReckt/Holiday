package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.Utilities;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class GeneralCommands {

    @Command(names = "playtime")
    public static void playtime(Player sender, @Param(name = "player", defaultValue = "self") Player target) {
        long playtime = target.getStatistic(Statistic.PLAY_ONE_TICK) * 50L;

        String playtimeString = target == sender ? Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.PLAYTIME.SELF") :
                Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.PLAYTIME.OTHER");
        playtimeString = playtimeString.replace("<playtime>", TimeUtil.formatDuration(playtime)).replace("<player>", Holiday.getInstance().getProfileHandler().getByUUID(target.getUniqueId()).getDisplayNameWithColor());

        sender.sendMessage(CC.translate(playtimeString));
    }

    @Command( names = {"ping", "ms", "latency"})
    public static void execute(Player sender, @Param(name = "target", defaultValue = "self") Player target)  {
        if(target != sender) {
            for (String s : Holiday.getInstance().getMessages().getStringList("COMMANDS.GENERAL.PING.OTHER")) {
                sender.sendMessage(CC.translate(
                        s.replace("<ping>", String.valueOf(Utilities.getPing(target)))
                                .replace("<player>", Holiday.getInstance().getProfileHandler().getByPlayer(target).getDisplayNameWithColor())
                                .replace("<difference>",
                                        String.valueOf((Math.max(Utilities.getPing(sender), Utilities.getPing(target)) - Math.min(Utilities.getPing(sender), Utilities.getPing(target))))
                                )));
            }
        } else {
            sender.sendMessage(CC.translate(
                    Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.PING.SELF").replace("<ping>", String.valueOf(Utilities.getPing(target)))));
        }
    }

}
