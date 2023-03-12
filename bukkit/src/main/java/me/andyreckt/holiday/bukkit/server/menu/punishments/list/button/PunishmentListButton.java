package me.andyreckt.holiday.bukkit.server.menu.punishments.list.button;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.punishment.Punishment;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PunishmentListButton extends Button {

    Punishment data;

    public PunishmentListButton(IPunishment punishData) {
        this.data = (Punishment) punishData;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        Profile target = Holiday.getInstance().getApi().getProfile(data.getPunished());
        return new ItemBuilder(Material.SKULL_ITEM)
                .durability(SkullType.PLAYER.ordinal())
                .owner(Holiday.getInstance().getUuidCache().name(data.getPunished()))
                .displayname(nameWithColor(data.getPunished()))
                .lore(" ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Type: " + CC.SECONDARY + data.getType().getName() + " " + CC.GRAY + "[#" + data.getId() + "]",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Target: " + CC.SECONDARY + UserConstants.getNameWithColor(target),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Duration: " + CC.SECONDARY + data.getDurationObject().getFormatted(),
                        " ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued By: " + CC.SECONDARY + nameWithColor(data.getAddedBy()),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued On: " + CC.SECONDARY + data.getAddedOn(),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued Reason: " + CC.SECONDARY + data.getAddedReason())
                .build();
    }

    private String nameWithColor(UUID uuid) {
        return UserConstants.getNameWithColor(Holiday.getInstance().getApi().getProfile(uuid));
    }
}
