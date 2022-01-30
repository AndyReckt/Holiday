package me.andyreckt.holiday.commands;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.command.Command;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class OtherCommands {

    @Command(names = {"garbage"}, perm = "holiday.garbage", async = true)
    public static void gc(CommandSender sender) {
        System.gc();
        sender.sendMessage(CC.translate("&aSuccessfully ran the garbage collector."));
    }
}
