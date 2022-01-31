package me.andyreckt.holiday.player.punishments.menu.list.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PunishmentListButton extends Button {

    PunishData data;

    public PunishmentListButton(PunishData punishData) {
        this.data = punishData;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .durability(SkullType.PLAYER.ordinal())
                .owner(data.getPunished().getName())
                .displayname(data.getPunished().getNameWithColor())
                .lore(
                        "&5" + data.getType().getName(),
                        " &eDuration: &d" + data.getDurationString(),
                        " &eStaff: " + data.getAddedBy().getNameWithColor(),
                        " &eReason: &d" + data.getAddedReason(),
                        " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt())
                )

                .build();
    }
}
