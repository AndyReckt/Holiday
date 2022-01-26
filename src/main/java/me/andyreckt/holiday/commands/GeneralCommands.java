package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.*;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GeneralCommands {

    private static final Map<UUID, Cooldown> reportCooldownMap = new HashMap<>();
    private static final Map<UUID, Cooldown> helpopCooldownMap = new HashMap<>();

    @Command(names = {"report"}, async = true)
    public static void report(Player sender, @Param(name = "target") Player target, @Param(name = "reason", wildcard = true) String reason) {

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (sender == target) {
            sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.REPORT.YOURSELF")));
            return;
        }

        if(reportCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = reportCooldownMap.get(sender.getUniqueId());
            if(oldCd.hasExpired()) reportCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.REPORT.COOLDOWN").replace("<time>", TimeUtil.getDuration(oldCd.getRemaining()))));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Holiday.getInstance().getSettings().getInteger("COOLDOWNS.HELPOP"));
        reportCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.REPORT.SUBMITTED")));
        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.ReportPacket(
                Holiday.getInstance().getProfileHandler().getByPlayer(sender).getDisplayNameWithColor(),
                Holiday.getInstance().getProfileHandler().getByPlayer(target).getDisplayNameWithColor(),
                reason,
                Holiday.getInstance().getSettings().getString("SERVER.NICENAME")
        ));
    }

    @Command(names = {"request", "helpop", "helpme", "question", "ask",}, async = true)
    public static void request(Player sender,  @Param(name = "reason", wildcard = true) String reason) {

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if(helpopCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = helpopCooldownMap.get(sender.getUniqueId());
            if(oldCd.hasExpired()) helpopCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.HELPOP.COOLDOWN").replace("<time>", TimeUtil.getDuration(oldCd.getRemaining()))));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Holiday.getInstance().getSettings().getInteger("COOLDOWNS.HELPOP"));
        helpopCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.HELPOP.SUBMITTED")));
        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.HelpopPacket(
                Holiday.getInstance().getProfileHandler().getByPlayer(sender).getDisplayNameWithColor(),
                reason,
                Holiday.getInstance().getSettings().getString("SERVER.NICENAME")
        ));
    }

    @Command(names = "playtime")
    public static void playtime(Player sender, @Param(name = "player", defaultValue = "self") Player target) {
        long playtime = target.getStatistic(Statistic.PLAY_ONE_TICK) * 50L;

        String playtimeString = target == sender ? Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.PLAYTIME.SELF") :
                Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.PLAYTIME.OTHER");
        playtimeString = playtimeString.replace("<playtime>", TimeUtil.getDuration(playtime)).replace("<player>", Holiday.getInstance().getProfileHandler().getByUUID(target.getUniqueId()).getDisplayNameWithColor());

        sender.sendMessage(CC.translate(playtimeString));
    }

    @Command( names = {"ping", "ms", "latency"})
    public static void ping(Player sender, @Param(name = "target", defaultValue = "self") Player target)  {
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

    @Command(names = {"who","list"}, async = true)
    public static void list(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        Rank[] ranks = (Rank[]) Arrays.copyOf(Holiday.getInstance().getRankHandler().ranks().toArray(), Holiday.getInstance().getRankHandler().ranks().size());
        ArrayUtils.reverse(ranks);

        Arrays.stream(ranks).filter(Rank::isVisible).forEach(rank
                -> builder.append(CC.translate(rank.getDisplayName())).append(CC.WHITE).append(", "));

        builder.setCharAt(builder.length() - 2, '.');

        builder.append("\n");

        List<String> players = PlayerList.getVisiblyOnline(sender)
                .visibleRankSorted().asColoredNames();

        builder.append(CC.R).append("(").append(PlayerList.getVisiblyOnline(sender).getPlayers().size()).append("/")
                .append(Holiday.getInstance().getServer().getMaxPlayers()).append("): ")
                .append("&f[")
                .append(players.toString().replace("[", "").replace("]", ""))
                .append("&f]");

        sender.sendMessage(CC.translate(builder.toString()));
    }

    @Command(names = "rename", perm = "holiday.rename")
    public static void rename(Player sender, @Param(name = "name", wildcard = true) String name) {
        ItemStack is = sender.getItemInHand();
        if (is == null || is.getType().equals(Material.AIR)) {
            sender.sendMessage(CC.translate("&cYou must hold an item in order to rename it."));
            return;
        }
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(CC.translate(name));

        String itemName = StringUtils.capitalize(is.getType().name().toLowerCase().replace("_", ""));


        sender.updateInventory();
        sender.sendMessage(CC.translate(
                Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.RENAME")
                        .replace("<item>", itemName)
                        .replace("<name>", name)
        ));
    }



}
