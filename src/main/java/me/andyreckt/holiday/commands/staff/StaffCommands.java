package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;

public class StaffCommands {

    @Command(names = {"alts"}, perm = "holiday.alts", async = true)
    public static void alts(CommandSender sender, @Param(name = "player") Profile target) {
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

    @Command(names = "killall", perm = "holiday.killall")
    public static void killall(CommandSender sender,
                               @Param(name = "<all|mobs|animals|items>", defaultValue = "all", tabCompleteFlags = {"all", "mobs", "animals", "items"}) String arg) {
        int total = 0;
        switch (arg) {
            case "all": {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Monster || entity instanceof Animals || entity instanceof Item) {
                            entity.remove();
                            total++;
                        }
                    }
                }
                sender.sendMessage(CC.translate("&aYou have removed all the entities."));
                sender.sendMessage(CC.translate("&aTotal&7: " + total));
                break;
            }
            case "mob":
            case "mobs": {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Monster) {
                            entity.remove();
                            total++;
                        }
                    }
                }
                sender.sendMessage(CC.translate("&aYou have removed all the mobs."));
                sender.sendMessage(CC.translate("&aTotal&7: " + total));
                break;
            }
            case "animals":
            case "animal": {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Animals) {
                            entity.remove();
                            total++;
                        }
                    }
                }
                sender.sendMessage(CC.translate("&aYou have removed all the animals."));
                sender.sendMessage(CC.translate("&aTotal&7: " + total));
                break;
            }
            case "items":
            case "item": {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Item) {
                            entity.remove();
                            total++;
                        }
                    }
                }

                sender.sendMessage(CC.translate("&aYou have removed all the items."));
                sender.sendMessage(CC.translate("&aTotal&7: " + total));
                break;
            }
        }
    }


}
