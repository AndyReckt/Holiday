package me.andyreckt.holiday.player.punishments.menu.check.button;

import io.github.zowpy.menu.Button;
import lombok.NonNull;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PunishmentCheckButton extends Button {

    PunishData data;

    public PunishmentCheckButton(PunishData punishData) {
        this.data = punishData;
    }

    @Override
    public ItemStack getButtonItem(@NonNull Player player) {
        switch (data.getType()) {
            case BAN:
            case TEMP_BAN: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore("&5Ban",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore("&5Ban &8(&cExpired&8)",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    " &eUnbanned by: &d" + data.getRemovedBy().getNameWithColor(),
                                    " &eUnban reason: &d" + data.getRemovedReason(),
                                    " &eUnban date: &d" + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
            case MUTE:
            case TEMP_MUTE: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore("&5Mute",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore("&5Mute &8(&cExpired&8)",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    " &eUnmuted by: &d" + data.getRemovedBy().getNameWithColor(),
                                    " &eUnmute reason: &d" + data.getRemovedReason(),
                                    " &eUnmute date: &d" + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
            case IP_BAN: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore("&5IP-BAN",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore("&5IP-BAN &8(&cExpired&8)",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    " &eUnbanned by: &d" + data.getRemovedBy().getNameWithColor(),
                                    " &eUnban reason: &d" + data.getRemovedReason(),
                                    " &eUnban date: &d" + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
            case BLACKLIST: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.DARK_RED + data.getId())
                            .lore("&5Blacklist",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.GOLD + data.getId())
                            .lore("&5Blacklist &8(&aExpired&8)",
                                    " &eDuration: &d" + data.getDurationString(),
                                    " &eStaff: &d" + data.getAddedBy().getNameWithColor(),
                                    " &eReason: &d" + data.getAddedReason(),
                                    " &eDate: &d" + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    " &eUnblacklist by: &d" + data.getRemovedBy().getNameWithColor(),
                                    " &eUnblacklist reason: &d" + data.getRemovedReason(),
                                    " &eUnblacklist date: &d" + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
        }
        return null;
    }
}
