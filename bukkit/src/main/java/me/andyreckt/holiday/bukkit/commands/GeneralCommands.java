package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.HelpopPacket;
import me.andyreckt.holiday.bukkit.server.redis.packet.ReportPacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.bukkit.util.player.PlayerList;
import me.andyreckt.holiday.bukkit.util.player.PlayerUtils;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GeneralCommands {

    private final Map<UUID, Cooldown> reportCooldownMap = new HashMap<>();
    private final Map<UUID, Cooldown> helpopCooldownMap = new HashMap<>();

    @Command(names = {"report"}, async = true)
    public void report(Player sender, @Param(name = "target") Player target, @Param(name = "reason", wildcard = true) String reason) {

        if (sender == target) {
            sender.sendMessage(Locale.CANNOT_REPORT_YOURSELF.getString());
            return;
        }

        if (reportCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = reportCooldownMap.get(sender.getUniqueId());
            if (oldCd.hasExpired()) reportCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(Locale.COOLDOWN.getString().replace("%time%", TimeUtil.getDuration(oldCd.getRemaining())));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Locale.REPORT_COOLDOWN.getInt());
        reportCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(Locale.REPORT_MESSAGE.getString());
        Profile profile = Holiday.getInstance().getApi().getProfile(sender.getUniqueId());
        Profile targetProfile = Holiday.getInstance().getApi().getProfile(target.getUniqueId());

        ReportPacket packet = new ReportPacket(
                Holiday.getInstance().getDisplayNameWithColor(profile),
                Holiday.getInstance().getDisplayNameWithColor(targetProfile),
                reason,
                Holiday.getInstance().getThisServer().getServerName()
        );

        Holiday.getInstance().getApi().getRedis().sendPacket(packet);
    }

    @Command(names = {"request", "helpop", "helpme", "question", "ask",}, async = true)
    public void request(Player sender, @Param(name = "reason", wildcard = true) String reason) {

        if (helpopCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = helpopCooldownMap.get(sender.getUniqueId());
            if (oldCd.hasExpired()) helpopCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(Locale.COOLDOWN.getString().replace("%time%", TimeUtil.getDuration(oldCd.getRemaining())));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Locale.HELPOP_COOLDOWN.getInt());
        helpopCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(Locale.HELPOP_MESSAGE.getString());
        Holiday.getInstance().getApi().getRedis().sendPacket(new HelpopPacket(
                Holiday.getInstance().getDisplayNameWithColor(Holiday.getInstance().getApi().getProfile(sender.getUniqueId())),
                reason,
                Holiday.getInstance().getThisServer().getServerName()
        ));
    }

    @Command(names = {"ping", "ms", "latency"})
    public void ping(Player sender, @Param(name = "target", baseValue = "self") Player target) {
        if (target != sender) {
            String diff = String.valueOf(Math.max(PlayerUtils.getPing(sender), PlayerUtils.getPing(target)) - Math.min(PlayerUtils.getPing(sender), PlayerUtils.getPing(target)));
            String str = Locale.PING_OTHER.getString()
                    .replace("%player%", target.getName())
                    .replace("%ping%", String.valueOf(PlayerUtils.getPing(target)))
                    .replace("%difference%", diff);

            sender.sendMessage(str);
        } else {
            sender.sendMessage(Locale.PING.getString().replace("%ping%", String.valueOf(PlayerUtils.getPing(sender))));
        }
    }

    @Command(names = {"who", "list"}, async = true)
    public void list(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        IRank[] ranks = Holiday.getInstance().getApi().getRanksSorted().toArray(new IRank[]{});

        Arrays.stream(ranks).filter(IRank::isVisible).forEach(rank
                -> builder.append(CC.translate(rank.getDisplayName())).append(CC.GRAY).append(", "));

        builder.setCharAt(builder.length() - 2, '.');

        builder.append("\n");

        List<String> players = PlayerList.getVisiblyOnline(sender)
                .visibleRankSorted().asColoredNames();

        builder.append(CC.GRAY).append("(").append(PlayerList.getVisiblyOnline(sender).getPlayers().size()).append("/")
                .append(Holiday.getInstance().getServer().getMaxPlayers()).append("): ")
                .append("&7[")
                .append(players.toString().replace("[", "").replace("]", ""))
                .append("&7]");

        sender.sendMessage(CC.translate(builder.toString()));
    }

    @Command(names = "rename", permission = Perms.RENAME)
    public void rename(Player sender, @Param(name = "name", wildcard = true) String name) {
        ItemStack is = sender.getItemInHand();
        if (is == null || is.getType().equals(Material.AIR)) {
            sender.sendMessage(CC.translate("&cYou must hold an item in order to rename it."));
            return;
        }
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(CC.translate(name));

        String itemName = StringUtils.capitalize(is.getType().name().toLowerCase().replace("_", " "));
        sender.getItemInHand().setItemMeta(im);

        sender.updateInventory();
        sender.sendMessage(CC.translate(
                Locale.RENAME.getString()
                        .replace("%item%", itemName)
                        .replace("%name%", name)
        ));
    }


}
