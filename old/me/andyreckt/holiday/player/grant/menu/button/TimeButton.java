package me.andyreckt.holiday.player.grant.menu.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.menu.GrantConfirmMenu;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtil;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TimeButton extends Button {

    final ChatColor color;
    final String time;
    final Profile profile;
    final Rank rank;

    public TimeButton(ChatColor color, String time, Profile profile, Rank rank) {
        this.color = color;
        this.time = time;
        this.profile = profile;
        this.rank = rank;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Material.WOOL)
                .displayname(color + TimeUtil.getDuration(TimeUtil.getDuration(time)))
                .damage(StringUtil.convertChatColorToWoolData(color))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        new GrantConfirmMenu(profile, rank, TimeUtil.getDuration(time)).openMenu(player);
    }
}
