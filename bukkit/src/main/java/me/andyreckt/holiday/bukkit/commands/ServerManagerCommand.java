package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.server.ServerListMenu;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@MainCommand(names = {"servermanager", "sm"}, permission = Perms.SERVERMANAGER, description = "Server Manager")
public class ServerManagerCommand {

    @SubCommand(names = {"command", "runcommand", "cmd"}, description = "Run a command on a server, or all the servers.")
    public void runCmd(CommandSender sender, @Param(name = "server") String serverid, @Param(name = "command", wildcard = true) String command) {
        if (!serverid.equalsIgnoreCase("ALL")) {
            Holiday.getInstance().getApi().getServer(serverid).thenAccept(server -> {
                if (server == null) {
                    sender.sendMessage(Locale.SERVER_NOT_FOUND.getString());
                    return;
                }
                String toSend = Locale.STAFF_SERVER_MANAGER_RUN_SERVER.getString()
                        .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                        .replace("%serverid%", Holiday.getInstance().getThisServer().getServerId())
                        .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                        .replace("%command%", command);
                Holiday.getInstance().getApi().getMidnight().sendObject(new BroadcastPacket(toSend, Perms.ADMIN_VIEW_NOTIFICATIONS.get()));
                sender.sendMessage(Locale.PLAYER_SERVER_MANAGER_RUN_SERVER.getString()
                        .replace("%server%", server.getServerName())
                        .replace("%command%", command));
            });
        } else {
            String toSend = Locale.STAFF_SERVER_MANAGER_RUN_ALL.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                    .replace("%command%", command);
            Holiday.getInstance().getApi().getMidnight().sendObject(new BroadcastPacket(toSend, Perms.ADMIN_VIEW_NOTIFICATIONS.get()));
            sender.sendMessage(Locale.PLAYER_SERVER_MANAGER_RUN_ALL.getString()
                    .replace("%command%", command));
        }
    }

    @SubCommand(names = {"info", "status"}, description = "Get information about a server.")
    public void info(CommandSender sender, @Param(name = "server") String serverid) {
        Holiday.getInstance().getApi().getServer(serverid).thenAccept(server -> {
            if (server == null) {
                sender.sendMessage(Locale.SERVER_NOT_FOUND.getString());
                return;
            }
            if (!server.isOnline()) {
                sender.sendMessage(Locale.PLAYER_SERVER_MANAGER_INFO_OFFLINE.getString());
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
        });
    }

    @SubCommand(names = {"list", "servers"}, description = "Get a list of all the servers.")
    public void list(Player sender) {
        Holiday.getInstance().getApi().getServers().thenAccept(servers -> new ServerListMenu(servers.values()).openMenu(sender));
    }




}
