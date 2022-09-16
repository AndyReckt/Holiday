package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.PermissionChangePacket;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.player.rank.menu.RankManageMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TextComponentBuilder;
import me.andyreckt.sunset.annotations.MainCommand;
import me.andyreckt.sunset.annotations.Param;
import me.andyreckt.sunset.annotations.SubCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@MainCommand(permission = "op", names = {"rank", "ranks"}, helpCommand = "help", description = "Rank management commands.")
public class RankCommands {

    private static final RankHandler rh = Holiday.getInstance().getRankHandler();

    @SubCommand(names = {"help"}, async = true)
    public static void onHelp(CommandSender sender) {

        String[] message = {
                "&cUsage: /rank manage <rank>",
                "&cUsage: /rank addperm/removeperm <rank> <permission>",
                "&cUsage: /rank addchild/removechild <rank> <child>",
                "&cUsage: /rank setpriority <rank> <priority>",
                "&cUsage: /rank create/delete <rank>",
                "&cUsage: /rank list"
                //TODO ADD RANK IMPORT/EXPORT
        };

        for (String s : message) {
            sender.sendMessage(CC.translate(s));
        }

    }

    @SubCommand(names = {"create"}, async = true)
    public static void create(CommandSender sender, @Param(name = "rank") String string) {

        if (rh.getFromName(string) != null) {
            sender.sendMessage(CC.translate("&cThe rank " + rh.getFromName(string).getDisplayName() + " already exists."));
            return;
        }

        Rank rank = rh.createRank(string);
        rank.save();
        sender.sendMessage(CC.translate("&aRank created."));

    }

    @SubCommand(names = {"manage", "edit"})
    public static void manage(Player sender, @Param(name = "rank") Rank rank) {
        new RankManageMenu(rank).openMenu(sender);
    }

    @SubCommand(names = {"list"})
    public static void list(Player sender) {
        sender.sendMessage(CC.translate("&aRank list: "));
        sender.sendMessage(CC.CHAT_BAR);
        for (Rank rank : rh.ranksSorted()) {
            TextComponentBuilder builder = new TextComponentBuilder(CC.translate(" " + rank.getDisplayName()));
            builder.setHoverEvent(HoverEvent.Action.SHOW_TEXT,
                    CC.CHAT + "Display Name: " + rank.getDisplayName() + "\n" +
                            CC.CHAT + "Prefix: " + rank.getPrefix() + "\n" +
                            CC.CHAT + "Suffix: " + rank.getSuffix() + "\n" +
                            CC.CHAT + "Color: " + rank.getColor() + rank.getColor().name() + "\n" +
                            CC.CHAT + "Priority: " + CC.PRIMARY + rank.getPriority() + "\n" +
                            CC.CHAT + "Default: " + yesNo(rank.isDefault()) + "\n" +
                            CC.CHAT + "Visible: " + yesNo(rank.isVisible()) + "\n" +
                            CC.CHAT + "Bold: " + yesNo(rank.isBold()) + "\n" +
                            CC.CHAT + "Italic: " + yesNo(rank.isItalic()) + "\n" +
                            CC.CHAT + "Staff: " + yesNo(rank.isStaff()) + "\n" +
                            CC.CHAT + "Admin: " + yesNo(rank.isAdmin()) + "\n" +
                            CC.CHAT + "Dev: " + yesNo(rank.isDev()) + "\n" +
                            "&7&oClick to manage this rank"
            );
            builder.setClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank manage " + rank.getName());
            sender.spigot().sendMessage(builder.toText());
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

    @SubCommand(names = {"addperm"}, async = true)
    public static void addperm(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "perm") String perm) {
        rank.addPermission(perm);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully added the permission \"" + perm + "\" to the rank " + rank.getDisplayName()));
    }

    @SubCommand(names = {"removeperm", "remperm"}, async = true)
    public static void removePerm(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "perm") String perm) {
        rank.removePermission(perm);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully removed the permission \"" + perm + "\" from the rank " + rank.getDisplayName()));
    }

    @SubCommand(names = {"addchild"}, async = true)
    public static void addChild(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "child") Rank child) {
        rank.removeChild(child);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully added the child \"" + child.getDisplayName() + "\"&a to the rank " + rank.getDisplayName()));
    }

    @SubCommand(names = {"removechild", "remchild"}, async = true)
    public static void remChild(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "child") Rank child) {
        rank.removeChild(child);
        rank.save();

        Holiday.getInstance().getRedis().sendPacket(new PermissionChangePacket(rank));

        sender.sendMessage(CC.translate("&aSuccessfully removed the child \"" + child.getDisplayName() + "\" from the rank " + rank.getDisplayName()));
    }

    @SubCommand(names = {"setpriority", "priority", "setweight", "weight"}, async = true)
    public static void remChild(CommandSender sender, @Param(name = "rank") Rank rank, @Param(name = "priority") int i) {
        rank.setPriority(i);
        rank.save();

        sender.sendMessage(CC.translate("&aSuccessfully set the priority to \"" + i + "\" for the rank " + rank.getDisplayName()));
    }

    @SubCommand(names = {"delete", "remove"}, async = true)
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
