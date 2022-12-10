package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.CrossServerCommandPacket;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.other.menu.InvseeMenu;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.menu.check.CheckMenu;
import me.andyreckt.holiday.player.punishments.menu.list.ListMenu;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.*;
import me.andyreckt.sunset.annotations.Command;
import me.andyreckt.sunset.annotations.Param;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.lang.management.ManagementFactory;

public class StaffCommands {

    @Command(names = {"alts", "alt", "accounts", "associatedaccounts", "listallaccounts"}, permission = "holiday.alts", async = true)
    public static void alts(CommandSender sender, @Param(name = "player") Profile target) {
        StringBuilder alts = new StringBuilder();
        alts.append("&7[");
        int i = 0;
        for (String alt : target.formatAlts()) {
            i++;
            if (i == target.getAlts().size()) {
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

    @Command(names = "killall", permission = "holiday.killall")
    public static void killall(CommandSender sender,
                               @Param(name = "<all|mobs|animals|items>", baseValue = "all", tabCompleteFlags = {"all", "mobs", "animals", "items"}) String arg) {
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

    @Command(names = {"setmaxplayer", "setslots"}, permission = "holiday.setslots")
    public static void slots(CommandSender sender, @Param(name = "player") int players) {
        StringUtils.setSlots(players);
        Holiday.getInstance().getServerHandler().save();
        sender.sendMessage(CC.translate("&aSuccessfully set the slots to \"" + players + "\""));
    }

    @Command(names = {"more"}, permission = "holiday.more")
    public static void more(Player sender) {
        ItemStack item = sender.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }
        if (item.getAmount() >= 64) {
            sender.sendMessage(CC.translate("&cYour item is already stacked."));
            return;
        }
        item.setAmount(64);
        sender.updateInventory();
        sender.sendMessage(CC.translate("&aYou have stacked your item."));
    }

    @Command(names = "sudo", permission = "holiday.sudo")
    public static void sudo(CommandSender sender, @Param(name = "target") Player target, @Param(name = "message", wildcard = true) String msg) {
        sender.sendMessage(CC.translate("&aYou forced \"" + target.getName() + "\" to say \"" + msg + "\""));
        target.chat(msg);
    }

    @Command(names = {"sudoall", "massay"}, permission = "holiday.sudoall")
    public static void suadoall(CommandSender sender, @Param(name = "message", wildcard = true) String msg) {
        Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> Holiday.getInstance().getProfileHandler().getByPlayer(player))
                .filter(profile -> !PunishmentUtils.checkMuted(profile))
                .map(Profile::getPlayer)
                .forEach(player -> player.chat(msg));
        sender.sendMessage(CC.translate("&aYou forced everyone to say \"" + msg + "\""));
    }

    @Command(names = {"invsee", "inv"}, permission = "holiday.invsee")
    public static void invsee(Player player, @Param(name = "player") Player target) {
        new InvseeMenu(player, target).openMenu(player);
    }

    @Command(names = {"staff", "staffmode", "modmode", "mod"}, permission = "holiday.modmode")
    public static void staff(Player player) {

        if (Holiday.getInstance().getStaffHandler().isInStaffMode(player)) {
            Tasks.runAsync(() -> player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("STAFF.MODMODE.DISABLED"))));
            Holiday.getInstance().getStaffHandler().destroy(player);
        } else {
            Tasks.runAsync(() -> player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("STAFF.MODMODE.ENABLED"))));
            Holiday.getInstance().getStaffHandler().init(player);
        }
    }

    @Command(names = {"vanish", "v", "poof"}, permission = "holiday.modmode")
    public static void vanish(Player player) {
        if (Holiday.getInstance().getStaffHandler().isInStaffMode(player))
            Holiday.getInstance().getStaffHandler().getStaffPlayer(player).vanish();
        else player.sendMessage(CC.translate("&cYou need to be in staff mode to be able to vanish."));
    }

    @Command(names = {"check", "c", "checkban", "checkpun", "checkpunishments", "punishments", "bancheck", "mutecheck", "punishmentcheck", "punishcheck", "pcheck"}, permission = "holiday.checkpunishments")
    public static void check(Player player, @Param(name = "player") Profile target) {
        new CheckMenu(target).open(player);
    }

    @Command(names = {"freeze", "ss"}, permission = "holiday.freeze")
    public static void execute(Player player, @Param(name = "player") Player target) {
        Holiday.getInstance().getStaffHandler().handleFreeze(target);
        if (target.hasMetadata("frozen")) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("FREEZE.STAFF.FROZEN").replace("<player>", target.getName())));
        } else {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("FREEZE.STAFF.UNFROZEN").replace("<player>", target.getName())));
        }
    }

    @Command(names = {"punishmentlist", "plist", "banlist", "mutelist", "blacklistlist"}, permission = "holiday.punishmentslist")
    public static void punishmentsList(Player player) {
        new ListMenu().open(player);
    }

    @Command(names = {"sc", "staffchat"}, permission = "holiday.staffchat", async = true)
    public static void staffchat(Player player, @Param(name = "message", wildcard = true) String message) {
        Profile profile = Holiday.getInstance().getProfileHandler().getByUUID(player.getUniqueId());
        String playerName = profile.getNameWithColor();
        String server = Holiday.getInstance().getSettings().getString("SERVER.NICENAME");
        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(
                Holiday.getInstance().getMessages().getString("STAFF.CHAT").replace("<server>", server).replace("<player>", playerName).replace("<message>", message),
                StaffMessageType.STAFF
        ));
    }

    @Command(names = "join", permission = "holiday.join")
    public static void join(Player sender, @Param(name = "server") String server) {
        Server serverData = Holiday.getInstance().getServerHandler().getServers().get(server);

        if (serverData == null || !Holiday.getInstance().getServerHandler().isOnline(server)) {
            sender.sendMessage(CC.translate("&cThis server doesn't exist or is not online!"));

            StringBuilder sb = new StringBuilder();
            sb.append("&cAvailable servers: ");
            for (String s : Holiday.getInstance().getServerHandler().getServers().keySet()) {
                if (Holiday.getInstance().getServerHandler().isOnline(s)) sb.append("&c").append(s).append("&7,");
            }
            sender.sendMessage(CC.translate(sb.substring(0, sb.length() - 3)));
            return;
        }
        sender.sendMessage(CC.translate("&aJoining \"" + server + "\"..."));
        Utilities.sendToServer(sender, server);


    }

    @Command(names = "pull", permission = "holiday.pull")
    public static void join(Player sender, @Param(name = "player") Profile player) {

        if (!player.isOnline()) {
            sender.sendMessage(CC.translate("&cThis player is not online."));
            return;
        }

        if (player.getCurrentServer() == null || player.getCurrentServer().equalsIgnoreCase("null")) {
            sender.sendMessage(CC.translate("&cThis player's server is null???"));
            return;
        }

        sender.sendMessage(CC.translate("&aPulling " + player.getDisplayNameWithColor() + "&a to your server..."));
        Holiday.getInstance().getRedis().sendPacket(new CrossServerCommandPacket("sendtoserver " + player.getDisplayName() + " " + Holiday.getInstance().getSettings().getString("SERVER.NAME"), player.getCurrentServer()));
    }

    @Command(names = "sendtoserver", permission = "holiday.sendtoserver")
    public static void send(CommandSender sender, @Param(name = "player") Player player, @Param(name = "server") String server) {
        Server serverData = Holiday.getInstance().getServerHandler().getServers().get(server);

        if (serverData == null || !Holiday.getInstance().getServerHandler().isOnline(server)) {
            sender.sendMessage(CC.translate("&cThis server doesn't exist or is not online!"));

            StringBuilder sb = new StringBuilder();
            sb.append("&cAvailable servers: ");
            for (String s : Holiday.getInstance().getServerHandler().getServers().keySet()) {
                if (Holiday.getInstance().getServerHandler().isOnline(s)) sb.append("&c").append(s).append("&7,");
            }
            sender.sendMessage(CC.translate(sb.substring(0, sb.length() - 3)));
            return;
        }
        sender.sendMessage(CC.translate("&aSending " + player.getName() + " to " + server + "..."));
        Utilities.sendToServer(player, server);

    }

    @Command(names = {"find", "search"}, permission = "holiday.find")
    public static void find(CommandSender sender, @Param(name = "player") Profile player) {
        if (player.isOnline()) {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.FIND.ONLINE")
                    .replace("<server>", player.getCurrentServer() == null ? "&cUnknown Server" : player.getCurrentServer())
                    .replace("<target>", player.getNameWithColor()));
        } else {
            sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.FIND.OFFLINE")
                    .replace("<target>", player.getNameWithColor()));
        }
    }

    @Command(names = {"lag", "serverlag"}, permission = "holiday.lag")
    public static void lag(CommandSender sender) {
        StringBuilder sb = new StringBuilder(" ");
        for (double tps : Holiday.getInstance().getNmsHandler().recentTps()) {
            sb.append(format(tps));
            sb.append(", ");
        }

        long serverTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        String uptime = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - serverTime, true, true);
        String tps = sb.substring(0, sb.length() - 2);

        Holiday.getInstance().getMessages().getStringList("COMMANDS.LAG.MESSAGE").forEach(s -> {
            if (s.equalsIgnoreCase("<worlds>")) {
                for (World world : Bukkit.getWorlds()) {
                    sender.sendMessage(CC.translate(
                            Holiday.getInstance().getMessages().getString("COMMANDS.LAG.WORLDS")
                                    .replace("<name>", world.getName())
                                    .replace("<chunks>", String.valueOf(world.getLoadedChunks().length))
                                    .replace("<entities>", String.valueOf(world.getEntities().size())))
                    );
                }
            } else
                sender.sendMessage(CC.translate(s
                        .replace("<bar>", CC.CHAT_BAR)
                        .replace("<tps>", tps)
                        .replace("<uptime>", uptime)
                        .replace("<mem_max>", String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024))
                        .replace("<mem_allocated>", String.valueOf(Runtime.getRuntime().totalMemory() / 1024 / 1024))
                        .replace("<mem_available>", String.valueOf(Runtime.getRuntime().freeMemory() / 1024 / 1024))
                ));


        });
    }

    static String format(double tps) {
        return ((tps > 18.0) ? CC.GREEN : (tps > 16.0) ? CC.YELLOW : CC.RED)
                + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }


}
