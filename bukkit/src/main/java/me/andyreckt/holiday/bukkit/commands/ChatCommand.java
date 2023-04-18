package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@CommandAlias("chat")
public class ChatCommand extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("clear")
    @CommandAlias("clearchat")
    @CommandPermission("core.command.chat.clear")
    @Description("Clears the chat")
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
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        PacketHandler.send(
                new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }

    @Subcommand("mute")
    @CommandAlias("mutechat")
    @CommandPermission("core.command.chat.mute")
    @Description("Mutes the chat")
    public void mutechat(CommandSender sender) {
        Holiday.getInstance().getChatManager().setChatMuted(!Holiday.getInstance().getChatManager().isChatMuted());
        Bukkit.broadcastMessage(Holiday.getInstance().getChatManager().isChatMuted() ? Locale.GLOBAL_CHAT_MUTED.getString() : Locale.GLOBAL_CHAT_UNMUTED.getString());
        String toSendMuted = Locale.STAFF_CHAT_MUTED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        String toSendUnmuted = Locale.STAFF_CHAT_UNMUTED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        PacketHandler.send(
                new BroadcastPacket(Holiday.getInstance().getChatManager().isChatMuted() ? toSendMuted : toSendUnmuted,
                        Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }



    @Subcommand("slow")
    @CommandAlias("slowchat")
    @CommandPermission("core.command.chat.slow")
    @Description("Slows the chat")
    public void slowchat(CommandSender sender, @Single @Name("time") Duration duration){

        if(duration.get() == 0L || duration.isPermanent()) {
            sender.sendMessage(Locale.TIME_FORMAT.getString());
            return;
        }
        Holiday.getInstance().getChatManager().setChatDelay(duration.get());
        Bukkit.broadcastMessage(Locale.GLOBAL_CHAT_SLOWED.getString().replace("%delay%", duration.toRoundedTime()));
        String toSend = Locale.STAFF_CHAT_SLOWED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                .replace("%delay%", duration.toSmallRoundedTime());
        PacketHandler.send(
                new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }

    @Subcommand("unslow")
    @CommandAlias("unslowchat")
    @CommandPermission("core.command.chat.slow")
    @Description("Unslows the chat")
    public void unslowchat(CommandSender sender){
        Holiday.getInstance().getChatManager().setChatDelay(0L);
        Bukkit.broadcastMessage(Locale.GLOBAL_CHAT_UNSLOWED.getString());
        String toSend = Locale.STAFF_CHAT_UNSLOWED.getString()
                .replace("%executor%", sender instanceof ConsoleCommandSender ? "Console" : UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                .replace("%server%", Holiday.getInstance().getThisServer().getServerName());
        PacketHandler.send(
                new BroadcastPacket(toSend, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
    }


}