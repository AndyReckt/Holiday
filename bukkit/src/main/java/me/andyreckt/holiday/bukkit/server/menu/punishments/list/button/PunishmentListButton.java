package me.andyreckt.holiday.bukkit.server.menu.punishments.list.button;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PunishmentListButton extends Button {

    IPunishment data;

    public PunishmentListButton(IPunishment punishData) {
        this.data = punishData;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .durability(SkullType.PLAYER.ordinal())
                .owner(Holiday.getInstance().getUuidCache().name(data.getPunished()))
                .displayname(nameWithColor(data.getPunished()))
                .lore(CC.SECONDARY + data.getType().getName(),
                        CC.CHAT + " Duration: " + CC.PRIMARY + TimeUtil.getDuration(data.getDuration()),
                        CC.CHAT + " Staff: " + nameWithColor(data.getAddedBy()),
                        CC.CHAT + " Reason: " + CC.PRIMARY + data.getAddedReason(),
                        CC.CHAT + " Date: " + CC.PRIMARY + TimeUtil.formatDate(data.getAddedAt()))
                .build();
    }

    private String nameWithColor(UUID uuid) {
        return Holiday.getInstance().getNameWithColor(Holiday.getInstance().getApi().getProfile(uuid));
    }
}
