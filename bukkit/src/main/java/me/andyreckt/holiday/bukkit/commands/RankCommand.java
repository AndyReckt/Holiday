package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.rank.RankManageMenu;
import me.andyreckt.holiday.bukkit.server.menu.rank.RankManagerMenu;
import me.andyreckt.holiday.bukkit.server.redis.packet.PermissionUpdatePacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
 
  
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.TextComponentBuilder;
import me.andyreckt.holiday.core.user.rank.Rank;
import me.andyreckt.holiday.core.util.json.GsonProvider;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

@CommandAlias("rank|ranks")
@CommandPermission("core.command.ranks")
public class RankCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("create|add|new")
    public void create(CommandSender sender, @Single @Name("rank") String string) {
        API api = Holiday.getInstance().getApi();
        if (api.getRank(string) != null) {
            sender.sendMessage(Locale.RANK_ALREADY_EXISTS.getString());
            return;
        }
        IRank rank = api.createRank(string);
        api.saveRank(rank);
        sender.sendMessage(Locale.RANK_SUCCESSFULLY_CREATED.getString().replace("%rank%", string));
    }

    @Subcommand("edit|manage")
    @CommandCompletion("@ranks")
    public void manage(CommandSender sen, @Single @Name("rank") IRank rank) {
        Player sender = (Player) sen;
        new RankManageMenu(rank).openMenu(sender);
    }

    @Subcommand("editor|manager")
    @Conditions("player")
    public void editor(CommandSender sen) {
        Player sender = (Player) sen;
        new RankManagerMenu().openMenu(sender);
    }

    @Subcommand("list|all")
    @Conditions("player")
    public void list(CommandSender sen) {
        Player sender = (Player) sen;
        API api = Holiday.getInstance().getApi();
        sender.sendMessage(CC.translate("&aRank list: "));
        sender.sendMessage(CC.CHAT_BAR);
        for (IRank rank : api.getRanksSorted()) {
            TextComponentBuilder builder = new TextComponentBuilder(CC.translate(" " + rank.getDisplayName()));
            builder.setHoverEvent(
                            CC.CHAT + "Display Name: " + rank.getDisplayName() + "\n" +
                            CC.CHAT + "Prefix: " + rank.getPrefix() + "\n" +
                            CC.CHAT + "Suffix: " + rank.getSuffix() + "\n" +
                            CC.CHAT + "Color: " + UserConstants.getRankColor(rank) + rank.getColor() + "\n" +
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

    @Subcommand("addperm|addpermission")
    public void addperm(CommandSender sender, @Single @Name("rank") IRank rank, @Single @Name("permission") String perm) {
        API api = Holiday.getInstance().getApi();
        if (rank.getPermissions().contains(perm)) {
            sender.sendMessage(Locale.RANK_PERMISSION_ALREADY_EXISTS.getString().replace("%rank%", rank.getName()).replace("%permission%", perm));
            return;
        }

        rank.addPermission(perm);
        api.saveRank(rank);

        PacketHandler.send(new PermissionUpdatePacket());
        sender.sendMessage(Locale.RANK_PERMISSION_ADDED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%permission%", perm));
    }


    @Subcommand("removeperm|removepermission|remperm|rempermission")
    public void removePerm(CommandSender sender, @Single @Name("rank") IRank rank, @Single @Name("permission") String perm) {
        API api = Holiday.getInstance().getApi();
        if (!rank.getPermissions().contains(perm)) {
            sender.sendMessage(Locale.RANK_PERMISSION_DOES_NOT_EXIST.getString()
                    .replace("%rank%", CC.translate(rank.getDisplayName()))
                    .replace("%permission%", perm));
            return;
        }

        rank.removePermission(perm);
        api.saveRank(rank);

        PacketHandler.send(new PermissionUpdatePacket());
        sender.sendMessage(Locale.RANK_PERMISSION_REMOVED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%perm%", perm));
    }

    @Subcommand("addchild|addinheritance|addchildrank|addchild")
    @CommandCompletion("@ranks @ranks")
    public void addChild(CommandSender sender, @Single @Name("rank") IRank rank, @Single @Name("child") IRank child) {
        API api = Holiday.getInstance().getApi();
        if (rank.getChilds().contains(child.getUuid())) {
            sender.sendMessage(Locale.RANK_INHERITANCE_ALREADY_EXISTS.getString()
                    .replace("%rank%", CC.translate(rank.getDisplayName()))
                    .replace("%child%", CC.translate(child.getDisplayName())));
            return;
        }

        rank.addChild(child.getUuid());
        api.saveRank(rank);

        PacketHandler.send(new PermissionUpdatePacket());

        sender.sendMessage(Locale.RANK_INHERITANCE_ADDED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%child%", CC.translate(child.getDisplayName())));
    }

    @Subcommand("removechild|removeinheritance|remchildrank|remchild")
    @CommandCompletion("@ranks @ranks")
    public void remChild(CommandSender sender, @Single @Name("rank") IRank rank, @Single @Name("child") IRank child) {
        API api = Holiday.getInstance().getApi();
        if (!rank.getChilds().contains(child.getUuid())) {
            sender.sendMessage(Locale.RANK_INHERITANCE_DOES_NOT_EXIST.getString()
                    .replace("%rank%", CC.translate(rank.getDisplayName()))
                    .replace("%child%", CC.translate(child.getDisplayName())));
            return;
        }

        rank.removeChild(child.getUuid());
        api.saveRank(rank);

        PacketHandler.send(new PermissionUpdatePacket());

        sender.sendMessage(Locale.RANK_INHERITANCE_REMOVED.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%child%", CC.translate(child.getDisplayName())));
    }

    @CommandCompletion("@ranks")
    @Subcommand("setpriority|priority|setweight|weight")
    public void remChild(CommandSender sender, @Single @Name("rank") IRank rank, @Single @Name("priority") int i) {
        API api = Holiday.getInstance().getApi();
        rank.setPriority(i);
        api.saveRank(rank);

        sender.sendMessage(Locale.RANK_PRIORITY_SET.getString()
                .replace("%rank%", CC.translate(rank.getDisplayName()))
                .replace("%priority%", String.valueOf(i)));
    }

    @CommandCompletion("@ranks")
    @Subcommand("delete|remove")
    public void remRank(CommandSender sender, @Single @Name("rank") IRank rank) {
        if (rank.isDefault()) {
            sender.sendMessage(CC.translate("&cYou cannot delete the default rank!"));
            return;
        }
        Holiday.getInstance().getApi().deleteRank(rank);
        sender.sendMessage(Locale.RANK_SUCCESSFULLY_DELETED.getString().replace("%rank%", CC.translate(rank.getDisplayName())));
    }

    @SneakyThrows
    @Subcommand("export")
    public void export(CommandSender sender) {
        File file = new File(Holiday.getInstance().getDataFolder(), "ranks.json");
        if (!file.exists()) {
            Holiday.getInstance().saveResource("ranks.json", false);
        }
        Set<Rank> ranks = new HashSet<>();
        Holiday.getInstance().getApi().getRanks().forEach(r -> ranks.add((Rank) r));
        Files.write(GsonProvider.GSON.newBuilder().setPrettyPrinting().create().toJson(ranks), file, Charsets.UTF_8);
        sender.sendMessage(CC.translate("&aSuccessfully exported ranks to &f" + file.getName()));
    }

    @SneakyThrows
    @Subcommand("import")
    public void importRanks(CommandSender sender) {
        File file = new File(Holiday.getInstance().getDataFolder(), "ranks.json");
        if (!file.exists()) {
            sender.sendMessage(CC.translate("&cThe ranks.json file does not exist!"));
            return;
        }

        Set<Rank> ranks = GsonProvider.GSON.fromJson(Files.toString(file, Charsets.UTF_8), new TypeToken<HashSet<Rank>>() {
        }.getType());
        Holiday.getInstance().getApi().getRanks().forEach(r -> Holiday.getInstance().getApi().deleteRank(r));
        ranks.forEach(r -> Holiday.getInstance().getApi().saveRank(r));
        sender.sendMessage(CC.translate("&aSuccessfully imported ranks from &f" + file.getName()));
    }


    private String yesNo(boolean bool) {
        return bool ? "&aYes" : "&cNo";
    }

}
