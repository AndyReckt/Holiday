package me.andyreckt.holiday.bukkit.server.menu.punishments.check.button;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import lombok.NonNull;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PunishmentCheckButton extends Button {

    private final IPunishment data;

    public PunishmentCheckButton(IPunishment punishData) {
        this.data = punishData;
    }

    @Override
    public ItemStack getButtonItem(@NonNull Player player) {
        switch (data.getType()) {
            case BAN: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Ban",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Ban &8(&cExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unbanned by: " + CC.PRIMARY + nameWithColor(data.getRevokedBy()),
                                    CC.CHAT + " Unban reason: " + CC.PRIMARY + data.getRevokedReason(),
                                    CC.CHAT + " Unban date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRevokedAt()))
                            .build();
                }
            }
            case MUTE: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Mute",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "Mute &8(&cExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unmuted by: " + CC.PRIMARY + nameWithColor(data.getRevokedBy()),
                                    CC.CHAT + " Unmute reason: " + CC.PRIMARY + data.getRevokedReason(),
                                    CC.CHAT + " Unmute date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRevokedAt()))
                            .build();
                }
            }
            case IP_BAN: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "IP-BAN",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.RED + data.getId())
                            .lore(CC.SECONDARY + "IP-BAN &8(&cExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unbanned by: " + CC.PRIMARY + nameWithColor(data.getRevokedBy()),
                                    CC.CHAT + " Unban reason: " + CC.PRIMARY + data.getRevokedReason(),
                                    CC.CHAT + " Unban date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRevokedAt()))
                            .build();
                }
            }
            case BLACKLIST: {
                if (data.isActive()) {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 5)
                            .displayname(CC.DARK_RED + data.getId())
                            .lore(CC.SECONDARY + "Blacklist",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                            .build();
                } else {
                    return new ItemBuilder(Material.WOOL)
                            .durability((short) 14)
                            .displayname(CC.GOLD + data.getId())
                            .lore(CC.SECONDARY + "Blacklist &8(&aExpired&8)",
                                    CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                                    CC.CHAT + " Staff: " + CC.PRIMARY + nameWithColor(data.getAddedBy()),
                                    CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                                    CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()),
                                    " ",
                                    CC.CHAT + " Unblacklist by: " + CC.PRIMARY + nameWithColor(data.getRevokedBy()),
                                    CC.CHAT + " Unblacklist reason: " + CC.PRIMARY + data.getRevokedReason(),
                                    CC.CHAT + " Unblacklist date: " + CC.PRIMARY + TimeUtil.formatDate(data.getRevokedAt()))
                            .build();
                }
            }
        }
        return null;
    }


    private String nameWithColor(UUID uuid) {
        return UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(uuid));
    }
}
