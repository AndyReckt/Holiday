package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.server.IServer;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.server.ServerListMenu;
import me.andyreckt.holiday.bukkit.server.redis.packet.CrossServerCommandPacket;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@CommandAlias("servermanager|sm")
@CommandPermission("core.command.servermanager")
public class ServerManagerCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("command|runcommand|cmd")
    @CommandCompletion("@servers @nothing")
    public void runCmd(CommandSender sender, @Single @Name("server") String serverid, @Name("command") String command) {
        if (!serverid.equalsIgnoreCase("ALL")) {
            IServer server = Holiday.getInstance().getApi().getServer(serverid);
            if (server == null) {
                sender.sendMessage(Locale.SERVER_NOT_FOUND.getString());
                return;
            }
            String toSend = Locale.STAFF_SERVER_MANAGER_RUN_SERVER.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%serverid%", server.getServerId())
                    .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                    .replace("%command%", command);
            PacketHandler.send(
                    new BroadcastPacket(toSend, Perms.ADMIN_VIEW_NOTIFICATIONS.get(), AlertType.SERVER_MANAGER));
            sender.sendMessage(Locale.PLAYER_SERVER_MANAGER_RUN_SERVER.getString()
                    .replace("%server%", server.getServerName())
                    .replace("%command%", command));
            PacketHandler.send(new CrossServerCommandPacket(command, server.getServerId()));
        } else {
            String toSend = Locale.STAFF_SERVER_MANAGER_RUN_ALL.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                    .replace("%command%", command);
            PacketHandler.send(
                    new BroadcastPacket(toSend, Perms.ADMIN_VIEW_NOTIFICATIONS.get(), AlertType.SERVER_MANAGER));
            sender.sendMessage(Locale.PLAYER_SERVER_MANAGER_RUN_ALL.getString()
                    .replace("%command%", command));
            PacketHandler.send(new CrossServerCommandPacket(command, "ALL"));
        }
    }

    @CommandCompletion("@servers")
    @Subcommand("info|status")
    public void info(CommandSender sender, @Single @Name("server") String serverid) {
        IServer server = Holiday.getInstance().getApi().getServer(serverid);
        if (server == null || !server.isOnline()) {
            sender.sendMessage(Locale.SERVER_NOT_FOUND.getString());
            return;
        }

        String status = server.isWhitelisted() ? CC.CHAT + "Whitelisted" : CC.GREEN + "Online";

        StringBuilder sb = new StringBuilder(" ");
        for (double tps : server.getTps()) {
            sb.append(CC.formatTps(tps));
            sb.append(", ");
        }
        String tps = sb.substring(0, sb.length() - 2);

        Locale.PLAYER_SERVER_MANAGER_INFO.getStringList().forEach(s -> {
            s = s.replace("%name%", server.getServerName())
                    .replace("%id%", server.getServerId())
                    .replace("%status%", status)
                    .replace("%players%", server.getPlayerCount() + "")
                    .replace("%maxplayers%", server.getMaxPlayers() + "")
                    .replace("%tps%", tps);
            sender.sendMessage(CC.translate(s));
        });
    }

    @Subcommand("list|servers")
    @CommandPermission("core.command.servermanager")
    @CommandAlias("serverlist|serverlistgui|slgui|servers")
    @Conditions("player")
    public void list(CommandSender sender) {
        new ServerListMenu(Holiday.getInstance().getApi().getServers().values()).openMenu((Player) sender);
    }




}
