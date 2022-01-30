package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.command.CommandSender;
import org.omg.PortableInterceptor.HOLDING;

public class StaffCommands {

    @Command(names = {"alts"}, perm = "holiday.alts", async = true)
    public static void execute(CommandSender sender, @Param(name = "player") Profile target) {
        StringBuilder alts = new StringBuilder();
        alts.append("&7[");
        int i = 0;
        for (String alt : target.formatAlts()) {
            i++;
            if(i == target.getAlts().size()) {
                alts.append(alt);
            } else {
                alts.append(alt).append("&7, ");
            }
        }
        alts.append("&7] (").append(i).append(i == 1 ? " alt" : " alts").append(")");

        sender.sendMessage(CC.translate("&7[&aOnline&f, &7Offline&f, &eMuted&f, &cBanned&f, &4Blacklisted&7]"));
        sender.sendMessage(CC.translate("&7&oThe accounts associated to this profile are: "));
        sender.sendMessage(CC.translate(alts.toString()));
    }


}
