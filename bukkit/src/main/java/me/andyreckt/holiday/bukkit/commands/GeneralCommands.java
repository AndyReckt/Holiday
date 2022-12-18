package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.ReportPacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.other.Cooldown;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
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
                sender.sendMessage(Locale.COOLDOWN.getString().replace("%time%", TimeUtil.getDuration(oldCd.getRemaining()))));
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

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (helpopCooldownMap.containsKey(sender.getUniqueId())) {
            Cooldown oldCd = helpopCooldownMap.get(sender.getUniqueId());
            if (oldCd.hasExpired()) helpopCooldownMap.remove(sender.getUniqueId());
            else {
                sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.HELPOP.COOLDOWN").replace("<time>", TimeUtil.getDuration(oldCd.getRemaining()))));
                return;
            }
        }

        Cooldown cd = Cooldown.fromSeconds(Holiday.getInstance().getSettings().getInteger("COOLDOWNS.HELPOP"));
        helpopCooldownMap.put(sender.getUniqueId(), cd);

        sender.sendMessage(CC.translate(messages.getString("COMMANDS.GENERAL.HELPOP.SUBMITTED")));
        Holiday.getInstance().getRedis().sendPacket(new StaffMessages.HelpopPacket(
                Holiday.getInstance().getProfileHandler().getByPlayer(sender).getDisplayName(),
                reason,
                Holiday.getInstance().getSettings().getString("SERVER.NICENAME")
        ));
    }

    @Command(names = {"ping", "ms", "latency"})
    public void ping(Player sender, @Param(name = "target", baseValue = "self") Player target) {
        if (target != sender) {
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

    @Command(names = {"who", "list"}, async = true)
    public void list(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        Rank[] ranks = Holiday.getInstance().getRankHandler().ranks().toArray(new Rank[]{});

        Arrays.stream(ranks).sorted((o1, o2) -> -(o1.getPriority() - o2.getPriority())).filter(Rank::isVisible).forEach(rank
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

    @Command(names = "rename", permission = "holiday.rename")
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
                Holiday.getInstance().getMessages().getString("COMMANDS.GENERAL.RENAME")
                        .replace("<item>", itemName)
                        .replace("<name>", name)
        ));
    }


}
