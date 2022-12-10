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
                            .lore(CC.SECONDARY + "Ban",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Ban &8(&cExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unbanned by: " + CC.PRIMARY + data.getRemovedBy().getNameWithColor(),
                                    CC.CHAT + " Unban reason: " + CC.PRIMARY + data.getRemovedReason(),
                                    CC.CHAT + " Unban date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
            case MUTE:
            case TEMP_MUTE: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Mute",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Mute &8(&cExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unmuted by: " + CC.PRIMARY + data.getRemovedBy().getNameWithColor(),
                                    CC.CHAT + " Unmute reason: " + CC.PRIMARY + data.getRemovedReason(),
                                    CC.CHAT + " Unmute date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
            case IP_BAN: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "IP-BAN",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "IP-BAN &8(&cExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unbanned by: " + CC.PRIMARY + data.getRemovedBy().getNameWithColor(),
                                    CC.CHAT + " Unban reason: " + CC.PRIMARY + data.getRemovedReason(),
                                    CC.CHAT + " Unban date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
            case BLACKLIST: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.DARK_RED + data.getId())
                            .lore(CC.SECONDARY + "Blacklist",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.GOLD + data.getId())
                            .lore(CC.SECONDARY + "Blacklist &8(&aExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + data.getDurationString(),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + data.getAddedBy().getNameWithColor(),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unblacklist by: " + CC.PRIMARY + data.getRemovedBy().getNameWithColor(),
                                    CC.CHAT + " Unblacklist reason: " + CC.PRIMARY + data.getRemovedReason(),
                                    CC.CHAT + " Unblacklist date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRemovedAt()))
                            .build();
                }
            }
        }
        return null;
    }
}
