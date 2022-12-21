package me.andyreckt.holiday.bukkit.commands;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.nms.impl.NMS_v1_7_R4;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Command;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.MainCommand;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Param;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.SubCommand;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@MainCommand(names = {"gamemode", "gm"},
        permission = Perms.GAMEMODE,
        description = "Change your gamemode.")
public class GamemodeCommands {

    @SubCommand(names = {"creative", "c", "crea", "1"}, permission = Perms.GAMEMODE, description = "Change your gamemode to creative.", usage = "/gamemode creative [player]")
    @Command(names = {"gmc", "gm1"}, permission = Perms.GAMEMODE, description = "Change your gamemode to creative.", usage = "/gmc [player]")
    public void creative(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setGameMode(GameMode.CREATIVE);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"adventure", "a", "adv", "2"}, permission = Perms.GAMEMODE, description = "Change your gamemode to adventure.", usage = "/gamemode adventure [player]")
    @Command(names = {"gma", "gm2"}, permission = Perms.GAMEMODE)
    public void adventure(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setGameMode(GameMode.ADVENTURE);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"survival", "s", "surv" , "0"}, permission = Perms.GAMEMODE, description = "Change your gamemode to survival.", usage = "/gamemode survival [player]")
    @Command(names = {"gms", "gm0"}, permission = Perms.GAMEMODE)
    public void survival(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
        target.setGameMode(GameMode.SURVIVAL);
        sendMessage(sender, target);
    }

    @SubCommand(names = {"spectator", "sp", "spec", "3"}, permission = Perms.GAMEMODE, description = "Change your gamemode to spectator.", usage = "/gamemode spectator [player]")
    @Command(names = {"gmsp", "gm3"}, permission = Perms.GAMEMODE)
    public void spectator(CommandSender sender, @Param(name = "target", baseValue = "self") Player target) {
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
                    .replace("%executor%", Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())))
                    .replace("%gamemode%", target.getGameMode().name());
            Holiday.getInstance().getApi().getRedis().sendPacket(
                    new BroadcastPacket(string, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        } else {
            target.sendMessage(Locale.GAMEMODE_UPDATED_TARGET.getString().replace("%gamemode%", target.getGameMode().name()));
            sender.sendMessage(Locale.GAMEMODE_UPDATED_OTHER.getString().replace("%gamemode%", target.getGameMode().name()).replace("%player%", target.getName()));
            String string = Locale.STAFF_GAMEMODE_UPDATED.getString()
                    .replace("%server%", Holiday.getInstance().getThisServer().getServerName())
                    .replace("%executor%", sender instanceof Player ? Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(((Player) sender).getUniqueId())) : "Console")
                    .replace("%gamemode%", target.getGameMode().name())
                    .replace("%player%", target.getName());
            Holiday.getInstance().getApi().getRedis().sendPacket(
                    new BroadcastPacket(string, Perms.STAFF_VIEW_NOTIFICATIONS.get(), AlertType.ABUSE));
        }
    }
}