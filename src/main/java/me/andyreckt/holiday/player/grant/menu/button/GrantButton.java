package me.andyreckt.holiday.player.grant.menu.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GrantButton extends Button {

    final Grant grant;

    public GrantButton(Grant grant) {
        this.grant = grant;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        Profile issuer = ph.getByUUIDFor5Minutes(grant.getIssuer());

        ItemBuilder builder = new ItemBuilder(Material.WOOL)
                .displayname(grant.getRank().getDisplayName())
                .lore(
                        CC.MENU_BAR,
                        "&eAdded by: &d" + issuer.getName(),
                        "&eAdded at: &d" + TimeUtil.formatDate(grant.getExecutedAt()),
                        "&eDuration: &d" + TimeUtil.getDuration(grant.getDuration()),
                        "&eActive: " + yesNo(grant.isActive()),
                        CC.MENU_BAR
                )
                .damage(grant.isActive() ? 5 : 14);

        if (grant.isActive() && p0.hasPermission("holiday.grants.edit") && !grant.getRank().isDefault()) builder.lore("&7&oClick to remove this grant");

        return builder.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (!grant.isActive()) return;
        if (!player.hasPermission("holiday.grants.edit")) return;
        if (grant.getRank().isDefault()) return;

        grant.setActive(false);
        grant.save();
    }

    private String yesNo(boolean bool) {
        return bool ? "&aYes" : "&cNo";
    }
}
