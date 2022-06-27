package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PermissionChangePacket;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.player.rank.menu.RankManageMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TextComponentBuilder;
import me.andyreckt.holiday.utils.command.Command;
import me.andyreckt.holiday.utils.command.param.Param;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RankCommands {

    private static final RankHandler rh = Holiday.getInstance().getRankHandler();

    @Command(names = {"rank", "rank help"}, perm = "op", async = true)
    public static void onHelp(CommandSender sender) {

        String[] message = {
                "&cUsage: /rank manage <rank>",
                "&cUsage: /rank addperm/removeperm <rank> <permission>",
                "&cUsage: /rank addchild/removechild <rank> <child>",
                "&cUsage: /rank setpriority <priority>",
                "&cUsage: /rank create/delete <name>",
                "&cUsage: /rank list"
        };

        for (String s: message) {
            sender.sendMessage(CC.translate(s));
        }

    }

    @Command(names = {"rank create"}, perm = "op", async = true)
    public static void create(CommandSender sender, @Param(name = "rank") String string) {

        if (rh.getFromName(string) != null) {
            sender.sendMessage(CC.translate("&cThe rank " + rh.getFromName(string).getDisplayName() + " already exists."));
            return;
        }

        Rank rank = rh.createRank(string);
        rank.save();
        sender.sendMessage(CC.translate("&aRank created."));

    }

    @Command(names = {"rank manage", "rank edit"}, perm = "op")
    public static void manage(Player sender, @Param(name = "rank") Rank rank) {
        new RankManageMenu(rank).openMenu(sender);
    }

    @Command(names = {"rank list"}, perm = "op")
    public static void list(Player sender) {
        sender.sendMessage(CC.translate("&aRank list: "));
        sender.sendMessage(CC.CHAT_BAR);
        for (Rank rank : rh.ranksSorted()) {
            TextComponentBuilder builder = new TextComponentBuilder(CC.translate(" " + rank.getDisplayName()));
            builder.setHoverEvent(HoverEvent.Action.SHOW_TEXT,
                    "&eDisplay Name: " + rank.getDisplayName() + "\n" +
                            "&ePrefix: " + rank.getPrefix() + "\n" +
                            "&eSuffix: " + rank.getSuffix() + "\n" +
                            "&eColor: " + rank.getColor() + rank.getColor().name() + "\n" +
                            "&ePriority: &d" + rank.getPriority() + "\n" +
                            "&eDefault: " + yesNo(rank.isDefault()) + "\n" +
                            "&eVisible: " + yesNo(rank.isVisible()) + "\n" +
                            "&eBold: " + yesNo(rank.isBold()) + "\n" +
                            "&eItalic: " + yesNo(rank.isItalic()) + "\n" +
                            "&eStaff: " + yesNo(rank.isStaff()) + "\n" +
                            "&eAdmin: " + yesNo(rank.isAdmin()) + "\n" +
                            "&eDev: " + yesNo(rank.isDev()) + "\n" +
                            "&7&oClick to manage this rank"
            );
            builder.setClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank manage " + rank.getName());
            sender.spigot().sendMessage(builder.toText());
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

    @Command(names = {"rank addperm", "rank perm add"}, perm = "op", async = true)
    public static void addperm(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "perm") String perm) {
        rank.addPermission(perm);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully added the permission \"" + perm + "\" to the rank " + rank.getDisplayName()));
    }

    @Command(names = {"rank removeperm", "rank remperm", "rank perm rem", "rank perm remove"}, perm = "op", async = true)
    public static void removePerm(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "perm") String perm) {
        rank.removePermission(perm);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully removed the permission \"" + perm + "\" from the rank " + rank.getDisplayName()));
    }

    @Command(names = {"rank addchild", "rank child add"}, perm = "op", async = true)
    public static void addChild(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "child") Rank child) {
        rank.removeChild(child);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully added the child \"" + child.getDisplayName() + "\"&a to the rank " + rank.getDisplayName()));
    }

    @Command(names = {"rank removechild", "rank remchild", "rank child rem", "rank child remove"}, perm = "op", async = true)
    public static void remChild(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "child") Rank child) {
        rank.removeChild(child);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully removed the child \"" + child.getDisplayName() + "\" from the rank " + rank.getDisplayName()));
    }

    @Command(names = {"rank setpriority", "rank priority", "rank priority set", "rank weight set", "rank setweight", "rank weight"}, perm = "op", async = true)
    public static void remChild(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "priority") int i) {
        rank.setPriority(i);
        rank.save();

        sender.sendMessage(CC.translate("&aSuccessfully set the priority to \"" + i + "\" for the rank " + rank.getDisplayName()));
    }

    @Command(names = {"rank delete", "rank remove"}, perm = "op", async = true)
    public static void remRank(CommandSender sender, @Param(name = "rank") Rank rank) {
        if (rank.isDefault()) {
            sender.sendMessage(CC.translate("&cYou cannot delete the default rank!"));
            return;
        }
        Holiday.getInstance().getGrantHandler().deleteGrantsFromRank(rank);
        rh.deleteRank(rank);
        sender.sendMessage(CC.translate("&aSuccessfully deleted the rank " + rank.getDisplayName()));
    }


    private static String yesNo(boolean bool) {
        return bool ? "&aYes" : "&cNo";
    }


}
