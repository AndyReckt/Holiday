package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.other.menu.InvseeMenu;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PunishmentUtils;
import me.andyreckt.holiday.utils.StringUtils;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class StaffCommands {

    private static final BasicConfigurationFile messages = Holiday.getInstance().getMessages();

    @Command(names = {"alts", "alt", "accounts", "associatedaccounts", "listallaccounts"}, perm = "holiday.alts", async = true)
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
        alts.append("&7] (").append(i).append(i == 1 ? " account" : " accounts").append(")");

        sender.sendMessage(CC.translate("&7[&aOnline&7, &7Offline&7, &eMuted&7, &cBanned&7, &4Blacklisted&7]"));
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

    @Command(names = {"setmaxplayer", "setslots"}, perm = "holiday.setslots")
    public static void slots(CommandSender sender, @Param(name = "player") int players) {
        StringUtils.setSlots(players);
        Holiday.getInstance().getServerHandler().save();
        sender.sendMessage(CC.translate("&aSuccessfully set the slots to \"" + players + "\""));
    }

    @Command(names = {"more"}, perm = "holiday.more")
    public static void more(Player sender) throws Exception {
        ItemStack item = sender.getItemInHand();
        if(item == null || item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }
        if(item.getAmount() >= 64) {
            sender.sendMessage(CC.translate("&cYour item is already stacked."));
            return;
        }
        item.setAmount(64);
        sender.updateInventory();
        sender.sendMessage(CC.translate("&aYou have stacked your item."));
    }

    @Command(names = "sudo", perm = "holiday.sudo")
    public static void sudo(CommandSender sender, @Param(name = "target") Player target, @Param(name = "message", wildcard = true) String msg) {
        sender.sendMessage(CC.translate("&aYou forced \"" + target.getName() + "\" to say \"" + msg + "\""));
        target.chat(msg);
    }

    @Command(names = {"sudoall", "massay"}, perm = "holiday.sudoall")
    public static void suadoall(CommandSender sender, @Param(name = "message", wildcard = true) String msg) {
        Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> Holiday.getInstance().getProfileHandler().getByPlayer(player))
                .filter(profile -> !PunishmentUtils.checkMuted(profile))
                .map(Profile::getPlayer)
                .forEach(player -> player.chat(msg));
        sender.sendMessage(CC.translate("&aYou forced everyone to say \"" + msg + "\""));
    }

    @Command(names = {"invsee", "inv"}, perm = "holiday.invsee")
    public static void execute(Player player, @Param(name = "player") Player target) throws Exception {
        new InvseeMenu(player, target).openMenu(player);
    }





}
