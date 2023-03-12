package me.andyreckt.holiday.bukkit.server.menu.punishments.check.button;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import lombok.NonNull;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PunishmentCheckButton extends Button {

    private final Punishment data;

    public PunishmentCheckButton(IPunishment punishData) {
        this.data = (Punishment) punishData;
    }

    @Override
    public ItemStack getButtonItem(@NonNull Player player) {
        Profile target = Holiday.getInstance().getApi().getProfile(data.getPunished());
        if (data.isActive()) {
            return new ItemBuilder(Material.WOOL)
                    .durability((short) 5)
                    .displayname("&a(Active) " + TimeUtil.formatDate(data.getAddedAt()))
                    .lore(" ",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Type: " + CC.SECONDARY + data.getType().getName() + " " + CC.GRAY + "[#" + data.getId() + "]",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Target: " + CC.SECONDARY + UserConstants.getNameWithColor(target),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Duration: " + CC.SECONDARY + data.getDurationObject().getFormatted(),
                            " ",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued By: " + CC.SECONDARY + nameWithColor(data.getAddedBy()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued On: " + CC.SECONDARY + data.getAddedOn(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued Reason: " + CC.SECONDARY + data.getAddedReason())
                    .build();
        } else {
            return new ItemBuilder(Material.WOOL)
                    .durability((short) 14)
                    .displayname("&c(Inactive) " + TimeUtil.formatDate(data.getAddedAt()))
                    .lore("&c                  " + TimeUtil.formatDate(data.getRevokedAt()),
                            " ",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Type: " + CC.SECONDARY + data.getType().getName() + " " + CC.GRAY + "[#" + data.getId() + "]",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Target: " + CC.SECONDARY + UserConstants.getNameWithColor(target),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Duration: " + CC.SECONDARY + data.getDurationObject().getFormatted(),
                            " ",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued By: "     + CC.SECONDARY + nameWithColor(data.getAddedBy()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued On: "     + CC.SECONDARY + data.getAddedOn(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued Reason: " + CC.SECONDARY + data.getAddedReason(),
                            " ",
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Removed By: "     + CC.SECONDARY + nameWithColor(data.getRevokedBy()),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Removed On: "     + CC.SECONDARY + data.getRevokedOn(),
                            CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Removed Reason: " + CC.SECONDARY + data.getRevokedReason())
                    .build();
        }
    }


    private String nameWithColor(UUID uuid) {
        return UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(uuid));
    }
}
