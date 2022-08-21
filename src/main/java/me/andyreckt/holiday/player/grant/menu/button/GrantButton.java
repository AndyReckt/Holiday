package me.andyreckt.holiday.player.grant.menu.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.grant.menu.GrantsMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GrantButton extends Button {

    final Grant grant;
    final Profile profile;
    final boolean actives;

    public GrantButton(Grant grant, Profile profile, boolean actives) {
        this.grant = grant;
        this.profile = profile;
        this.actives = actives;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        ProfileHandler ph = Holiday.getInstance().getProfileHandler();
        Profile issuer = ph.getByUUIDFor5Minutes(grant.getIssuedBy());

        ItemBuilder item = new ItemBuilder(Material.WOOL);
        if (grant.isActive()) {
            item.displayname("&a(Active) " + TimeUtil.formatDate(grant.getIssuedAt()));
            item.lore(
                    CC.MENU_BAR,
                    CC.CHAT + "Rank: " + CC.PRIMARY + grant.getRank().getDisplayName(),
                    CC.CHAT + "Duration: " + CC.PRIMARY + TimeUtil.formatDate(grant.getDuration()),
                    CC.MENU_BAR,
                    CC.CHAT + "Issued By: " + CC.PRIMARY + issuer.getName(),
                    CC.CHAT + "Issued On: " + CC.PRIMARY + grant.getIssuedOn(),
                    CC.CHAT + "Issued Reason: " + CC.PRIMARY + grant.getReason(),
                    CC.MENU_BAR
            );
        } else {
            Profile remover = ph.getByUUIDFor5Minutes(grant.getRemovedBy());
            item.displayname("&c(Inactive) " + TimeUtil.formatDate(grant.getIssuedAt()));
            item.displayname("             " + TimeUtil.formatDate(grant.getRemovedAt()));
            item.lore(
                    CC.MENU_BAR,
                    CC.CHAT + "Rank: " + CC.PRIMARY + grant.getRank().getDisplayName(),
                    CC.CHAT + "Duration: " + CC.PRIMARY + TimeUtil.formatDate(grant.getDuration()),
                    CC.MENU_BAR,
                    CC.CHAT + "Issued By: " + CC.PRIMARY + issuer.getName(),
                    CC.CHAT + "Issued On: " + CC.PRIMARY + grant.getIssuedOn(),
                    CC.CHAT + "Issued Reason: " + CC.PRIMARY + grant.getReason(),
                    CC.MENU_BAR,
                    CC.CHAT + "Removed By: " + CC.PRIMARY + remover.getName(),
                    CC.CHAT + "Removed On: " + CC.PRIMARY + grant.getRemovedOn(),
                    CC.CHAT + "Removed Reason: " + CC.PRIMARY + grant.getRemovedReason(),
                    CC.MENU_BAR
            );
        }

        item.damage(grant.isActive() ? 5 : 14);

        if (grant.isActive() && p0.hasPermission("holiday.grants.edit") && !grant.getRank().isDefault())
            item.lore(CC.MENU_BAR,
                    "&cClick to remove this grant.",
                    CC.MENU_BAR
            );
        return item.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if (!grant.isActive()) return;
        if (!player.hasPermission("holiday.grants.edit")) return;
        if (grant.getRank().isDefault()) return;

        grant.setActive(false);
        grant.setRemovedBy(player.getUniqueId());
        grant.setRemovedOn(Holiday.getInstance().getServerHandler().getThisServer().getName());
        grant.setRemovedAt(System.currentTimeMillis());
        grant.setRemovedReason("Removed");
        grant.save();
        new GrantsMenu(profile, actives).openMenu(player);
    }
}
