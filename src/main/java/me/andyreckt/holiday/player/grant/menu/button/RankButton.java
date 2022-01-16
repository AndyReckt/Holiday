package me.andyreckt.holiday.player.grant.menu.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.menu.GrantChooseTimeMenu;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RankButton extends Button {

    final Profile profile;
    final Rank rank;

    public RankButton(Profile profile, Rank rank) {
        this.profile = profile;
        this.rank = rank;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        return new ItemBuilder(Material.WOOL)
                .damage(StringUtil.convertChatColorToWoolData(rank.getColor()))
                .displayname(rank.getDisplayName())
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        new GrantChooseTimeMenu(profile, rank).openMenu(player);
    }
}
