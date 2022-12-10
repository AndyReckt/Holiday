package me.andyreckt.holiday.player.staff.menu.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaffButton extends Button {

    private final Profile profile;

    public StaffButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .displayname(profile.getNameWithColor())
                .damage(SkullType.PLAYER.ordinal())
                .owner(profile.getName())
                .lore(
                        CC.CHAT + "Vanished: " + yesNo(profile.isVanished())
                ).build();
    }

    private String yesNo(boolean bool) {
        return bool ? "&aYes" : "&cNo";
    }
}
