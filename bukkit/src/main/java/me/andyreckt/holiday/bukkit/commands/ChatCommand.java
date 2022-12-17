package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@MainCommand(names = "chat",
        permission = Perms.CHAT,
        description = "Chat management commands.",
        usage = "/chat (clear|slow|unslow|mute)")
public class ChatCommand {

    @SubCommand(names = "clear", permission = Perms.CHAT_CLEAR, async = true, usage = "/chat clear", description = "Clears the chat")
    @Command(names = "clearchat", permission = Perms.CHAT_CLEAR, async = true, usage = "/clearchat", description = "Clears the chat")
    public void clear(CommandSender sender) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
            if (!profile.isStaff()) {
                for (int i = 0; i < 1000; i++) {
                    player.sendMessage(" ");
                }
            }
        });

        Bukkit.broadcastMessage(Locale.GLOBAL_CHAT_CLEAR.getString());
        String toSend = Locale.STAFF_CHAT_CLEAR.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @SubCommand(names = "mute", permission = Perms.CHAT_MUTE, async = true, usage = "/chat mute", description = "Mutes the chat")
    @Command(names = {"mutechat"}, permission = Perms.CHAT_MUTE, async = true, usage = "/mutechat", description = "Mutes the chat")
    public void mutechat(CommandSender sender) {
        Holiday.getInstance().getChatManager().setChatMuted(!Holiday.getInstance().getChatManager().isChatMuted());
        Bukkit.broadcastMessage(Holiday.getInstance().getChatManager().isChatMuted() ? Locale.GLOBAL_CHAT_MUTED.getString() : Locale.GLOBAL_CHAT_UNMUTED.getString());
        String toSendMuted = Locale.STAFF_CHAT_MUTED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        String toSendUnmuted = Locale.STAFF_CHAT_UNMUTED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(Holiday.getInstance().getChatManager().isChatMuted() ? toSendMuted : toSendUnmuted, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @SubCommand(names = "slow", permission = Perms.CHAT_SLOW, async = true, usage = "/chat slow (duration)", description = "Slows the chat")
    @Command(names = {"slowchat"}, permission = Perms.CHAT_SLOW, async = true, usage = "/slowchat (duration)", description = "Slows the chat")
    public void slowchat(CommandSender sender, @Param(name = "time") String duration){

        long time = TimeUtil.getDuration(duration);
        if(time == 0L || time == -1L) {
            sender.sendMessage(Locale.TIME_FORMAT.getString());
            return;
        }
        Holiday.getInstance().getChatManager().setChatDelay(time);
        Bukkit.broadcastMessage(Locale.GLOBAL_CHAT_SLOWED.getString().replace("%delay%", TimeUtil.getDuration(time)));
        String toSend = Locale.STAFF_CHAT_SLOWED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%delay%", TimeUtil.getDuration(time));
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }

    @SubCommand(names = {"unslow"}, permission = Perms.CHAT_SLOW, async = true, usage = "/chat unslow", description = "Unslows the chat")
    @Command(names = {"unslowchat"}, permission = Perms.CHAT_SLOW, async = true, usage = "/unslowchat", description = "Unslows the chat")
    public void unslowchat(CommandSender sender){

        Holiday.getInstance().getChatManager().setChatDelay(0L);
        Bukkit.broadcastMessage(Locale.GLOBAL_CHAT_UNSLOWED.getString());
        String toSend = Locale.STAFF_CHAT_UNSLOWED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get()));
    }


}