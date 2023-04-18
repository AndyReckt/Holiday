package me.andyreckt.holiday.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_7_R4;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
 
  
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gm|gamemode")
@CommandPermission("core.command.gamemode")
public class GamemodeCommands extends BaseCommand {

    @HelpCommand
    @Syntax("[page]")
    public void doHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @CommandAlias("gmc|gm1")
    @Subcommand("creative|c|crea|1")
    @CommandPermission("core.command.gamemode")
    @CommandCompletion("@players")
    @Conditions("player")
    public void creative(CommandSender sender, @Name("target") @Default("self") @Single Player target) {
        target.setGameMode(GameMode.CREATIVE);
        sendMessage(sender, target);
    }

    @CommandPermission("core.command.gamemode")
    @Subcommand("adventure|a|adv|2")
    @CommandCompletion("@players")
    @CommandAlias("gma|gm2")
    @Conditions("player")
    public void adventure(CommandSender sender, @Name("target") @Default("self") @Single Player target) {
        target.setGameMode(GameMode.ADVENTURE);
        sendMessage(sender, target);
    }

    @CommandCompletion("@players")
    @CommandAlias("gms|gm0")
    @Subcommand("survival|s|surv|0")
    @CommandPermission("core.command.gamemode")
    @Conditions("player")
    public void survival(CommandSender sender, @Name("target") @Default("self") @Single Player target) {
        target.setGameMode(GameMode.SURVIVAL);
        sendMessage(sender, target);
    }

    @CommandCompletion("@players")
    @Subcommand("spectator|sp|spec|3")
    @CommandPermission("core.command.gamemode")
    @CommandAlias("gmsp|gm3")
    @Conditions("player")
    public void spectator(CommandSender sender, @Name("target") @Default("self") @Single Player target) {
        if (Holiday.getInstance().getNms() instanceof NMS_v1_7_R4) {
            sender.sendMessage(CC.RED + "Spectator mode is not supported on 1.7.");
            return;
        }
        target.setGameMode(GameMode.SPECTATOR);
        sendMessage(sender, target);
    }

    private void sendMessage(CommandSender sender, Player target) {
        if (sender instanceof Player && sender == target) {
            sender.sendMessage(Locale.GAMEMODE_UPDATED_SELF.getString().replace("%gamemode%", target.getGameMode().name()));
            String string = Locale.STAFF_GAMEMODE_UPDATED_SELF.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                    .replace("%gamemode%", target.getGameMode().name());
            PacketHandler.send(
                    new BroadcastPacket(string, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        } else {
            target.sendMessage(Locale.GAMEMODE_UPDATED_TARGET.getString().replace("%gamemode%", target.getGameMode().name()));
            sender.sendMessage(Locale.GAMEMODE_UPDATED_OTHER.getString().replace("%gamemode%", target.getGameMode().name()).replace("%player%", target.getName()));
            String string = Locale.STAFF_GAMEMODE_UPDATED.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", sender instanceof Player ? UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())) : "Console")
                    .replace("%gamemode%", target.getGameMode().name())
                    .replace("%player%", target.getName());
            PacketHandler.send(
                    new BroadcastPacket(string, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        }
    }
}