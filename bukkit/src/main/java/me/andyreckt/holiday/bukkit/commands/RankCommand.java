package me.andyreckt.holiday.bukkit.commands;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.rank.RankManageMenu;
import me.andyreckt.holiday.bukkit.server.menu.rank.RankManagerMenu;
import me.andyreckt.holiday.bukkit.util.Logger;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.TextComponentBuilder;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@MainCommand(names = {"rank", "ranks"}, description = "Manage ranks.", permission = Perms.RANKS)
public class RankCommand { //TODO: Rank import/export

    @SubCommand(names = {"create", "add", "new"}, async = true, description = "Create a new rank.", usage = "/rank create <rank>")
    public void create(CommandSender sender, @Param(name = "rank") String string) {
        API api = Holiday.getInstance().getApi();
        if (api.getRank(string) != null) {
            sender.sendMessage(Locale.RANK_ALREADY_EXISTS.getString());
            return;
        }
        IRank rank = api.createRank(string);
        api.saveRank(rank);
        sender.sendMessage(Locale.RANK_SUCCESSFULLY_CREATED.getString().replace("%rank%", string));
    }

    @SubCommand(names = {"edit", "manage"}, description = "Manage a rank.", usage = "/rank manage <rank>")
    public void manage(Player sender, @Param(name = "rank") IRank rank) {
        new RankManageMenu(rank).openMenu(sender);
    }

    @SubCommand(names = {"editor", "manager"}, description = "Manage ranks.", usage = "/rank manager")
    public void editor(Player sender) {
        new RankManagerMenu().openMenu(sender);
    }

    @SubCommand(names = {"list"}, description = "List all ranks.", usage = "/rank list")
    public void list(Player sender) {
        API api = Holiday.getInstance().getApi();
        sender.sendMessage(CC.translate("&aRank list: "));
        sender.sendMessage(CC.CHAT_BAR);
        for (IRank rank : api.getRanksSorted()) {
            TextComponentBuilder builder = new TextComponentBuilder(CC.translate(" " + rank.getDisplayName()));
            builder.setHoverEvent(HoverEvent.Action.SHOW_TEXT,
                    CC.CHAT + "Display Name: " + rank.getDisplayName() + "\n" +
                            CC.CHAT + "Prefix: " + rank.getPrefix() + "\n" +
                            CC.CHAT + "Suffix: " + rank.getSuffix() + "\n" +
                            CC.CHAT + "Color: " + Holiday.getInstance().getRankColor(rank) + rank.getColor() + "\n" +
                            CC.CHAT + "Priority: " + CC.PRIMARY + rank.getPriority() + "\n" +
                            CC.CHAT + "Default: " + yesNo(rank.isDefault()) + "\n" +
                            CC.CHAT + "Visible: " + yesNo(rank.isVisible()) + "\n" +
                            CC.CHAT + "Bold: " + yesNo(rank.isBold()) + "\n" +
                            CC.CHAT + "Italic: " + yesNo(rank.isItalic()) + "\n" +
                            CC.CHAT + "Staff: " + yesNo(rank.isStaff()) + "\n" +
                            CC.CHAT + "Admin: " + yesNo(rank.isAdmin()) + "\n" +
                            CC.CHAT + "Op: " + yesNo(rank.isOp()) + "\n" +
                            "&7&oClick to manage this rank"
            );
            builder.setClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank manage " + rank.getName());
            sender.spigot().sendMessage(builder.toText());
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

    @SubCommand(names = {"addperm", "addpermission"}, async = true, description = "Add a permission to a rank.", usage = "/rank addperm <rank> <permission>")
    public void addperm(CommandSender sender, @Param(name = "rank") IRank rank, @Param(name = "perm") String perm) {
        API api = Holiday.getInstance().getApi();
        if (rank.getPermissions().contains(perm)) {
            sender.sendMessage(Locale.RANK_PERMISSION_ALREADY_EXISTS.getString().replace("%rank%", rank.getName()).replace("%perm%", perm));
            return;
        }

        rank.addPermission(perm);
        api.saveRank(rank);

//        Holiday.getInstance().getApi().getMidnight().sendPacket(new PermissionChangePacket(rank)); //TODO: Implement this packet.

        sender.sendMessage(Locale.RANK_PERMISSION_ADDED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%perm%", perm));
    }

    @SubCommand(names = {"removeperm", "remperm", "removepermission", "rempermission"}, async = true, description = "Remove a permission from a rank.", usage = "/rank removeperm <rank> <permission>")
    public void removePerm(CommandSender sender, @Param(name = "rank") IRank rank, @Param(name = "perm") String perm) {
        API api = Holiday.getInstance().getApi();
        if (!rank.getPermissions().contains(perm)) {
            sender.sendMessage(Locale.RANK_PERMISSION_DOES_NOT_EXIST.getString()
                    .replace("%rank%", CC.translate(rank.getDisplayName()))
                    .replace("%perm%", perm));
            return;
        }

        rank.addPermission(perm);
        api.saveRank(rank);

//        Holiday.getInstance().getApi().getMidnight().sendPacket(new PermissionChangePacket(rank)); //TODO: Implement this packet.

        sender.sendMessage(Locale.RANK_PERMISSION_REMOVED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%perm%", perm));
    }

    @SubCommand(names = {"addchild"}, async = true, description = "Add a child rank to a rank.", usage = "/rank addchild <rank> <child>")
    public void addChild(CommandSender sender, @Param(name = "rank") IRank rank, @Param(name = "child") IRank child) {
        API api = Holiday.getInstance().getApi();
        if (rank.getChilds().contains(child.getUuid())) {
            sender.sendMessage(Locale.RANK_INHERITANCE_ALREADY_EXISTS.getString()
                    .replace("%rank%", CC.translate(rank.getDisplayName()))
                    .replace("%child%", CC.translate(child.getDisplayName())));
            return;
        }

        rank.addChild(child.getUuid());
        api.saveRank(rank);

//        Holiday.getInstance().getApi().getMidnight().sendPacket(new PermissionChangePacket(rank)); //TODO: Implement this packet.

        sender.sendMessage(Locale.RANK_INHERITANCE_ADDED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%child%", CC.translate(child.getDisplayName())));
    }

    @SubCommand(names = {"removechild", "remchild"}, async = true, description = "Remove a child rank from a rank.", usage = "/rank removechild <rank> <child>")
    public void remChild(CommandSender sender, @Param(name = "rank") IRank rank, @Param(name = "child") IRank child) {
        API api = Holiday.getInstance().getApi();
        if (!rank.getChilds().contains(child.getUuid())) {
            sender.sendMessage(Locale.RANK_INHERITANCE_DOES_NOT_EXIST.getString()
                    .replace("%rank%", CC.translate(rank.getDisplayName()))
                    .replace("%child%", CC.translate(child.getDisplayName())));
            return;
        }

        rank.removeChild(child.getUuid());
        api.saveRank(rank);

//        Holiday.getInstance().getApi().getMidnight().sendPacket(new PermissionChangePacket(rank)); //TODO: Implement this packet.

        sender.sendMessage(Locale.RANK_INHERITANCE_REMOVED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%child%", CC.translate(child.getDisplayName())));
    }

    @SubCommand(names = {"setpriority", "priority", "setweight", "weight"}, async = true, description = "Set the priority of a rank.", usage = "/rank setpriority <rank> <priority>")
    public void remChild(CommandSender sender, @Param(name = "rank") IRank rank, @Param(name = "priority") int i) {
        API api = Holiday.getInstance().getApi();
        rank.setPriority(i);
        api.saveRank(rank);

        sender.sendMessage(Locale.RANK_PRIORITY_SET.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%priority%", String.valueOf(i)));
    }

    @SubCommand(names = {"delete", "remove"}, async = true, description = "Delete a rank.", usage = "/rank delete <rank>")
    public void remRank(CommandSender sender, @Param(name = "rank") IRank rank) {
        if (rank.isDefault()) {
            sender.sendMessage(CC.translate("&cYou cannot delete the default rank!"));
            return;
        }
        Holiday.getInstance().getApi().deleteRank(rank);
        sender.sendMessage(Locale.RANK_SUCCESSFULLY_DELETED.getString().replace("%rank%", CC.translate(rank.getDisplayName())));
    }

    @SneakyThrows
    @SubCommand(names = "export", description = "Export all the ranks to a file.", usage = "/rank export")
    public void export(CommandSender sender) {
        File file = new File(Holiday.getInstance().getDataFolder(), "ranks.json");
        if (!file.exists()) {
            Holiday.getInstance().saveResource("ranks.json", false);
        }
        Queue<Rank> ranks = new ConcurrentLinkedQueue<>();
        Holiday.getInstance().getApi().getRanks().forEach(r -> ranks.add((Rank) r));
        Files.write(GsonProvider.GSON.toJson(ranks), file, Charsets.UTF_8);
        sender.sendMessage(CC.translate("&aSuccessfully exported ranks to &f" + file.getName()));
    }

    @SneakyThrows
    @SubCommand(names = "import", description = "Import all the ranks from a file.", usage = "/rank import")
    public void importRanks(CommandSender sender) {
        File file = new File(Holiday.getInstance().getDataFolder(), "ranks.json");
        if (!file.exists()) {
            sender.sendMessage(CC.translate("&cThe ranks.json file does not exist!"));
            return;
        }

        Queue<Rank> ranks = GsonProvider.GSON.fromJson(Files.toString(file, Charsets.UTF_8), new TypeToken<ConcurrentLinkedQueue<Rank>>() {
        }.getType());
        Holiday.getInstance().getApi().getRanks().forEach(r -> Holiday.getInstance().getApi().deleteRank(r));
        ranks.forEach(r -> Holiday.getInstance().getApi().saveRank(r));
        sender.sendMessage(CC.translate("&aSuccessfully imported ranks from &f" + file.getName()));
    }


    private String yesNo(boolean bool) {
        return bool ? "&aYes" : "&cNo";
    }

}
