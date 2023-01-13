package me.andyreckt.holiday.bukkit.server.menu.grant;

import me.andyreckt.holiday.api.API;
import me.andyreckt.holiday.api.user.IGrant;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GrantsMenu extends PaginatedMenu {

    private final Profile target;
    private final boolean actives;

    public GrantsMenu(Profile target, boolean actives) {
        this.target = target;
        this.actives = actives;
        this.setGlassColor(4);
    }

    public GrantsMenu(Profile target) {
        this(target, true);
    }


    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Grants - " + target.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> toReturn = new HashMap<>();
        if (actives) {
            for (IGrant grant : target.getActiveGrants()) {
                toReturn.put(toReturn.size(), new GrantButton(grant, target, actives));
            }
        } else {
            for (IGrant grant : target.getGrants()) {
                toReturn.put(toReturn.size(), new GrantButton(grant, target, actives));
            }
        }
        return toReturn;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();
        toReturn.put(4, new EasyButton(new ItemBuilder(Material.PAPER)
                .displayname(actives ? "&aActive grants" : "&bAll grants")
                .lore(actives ? "&7&oClick to switch to all grants" : "&7&oClick to switch to actives grants")
                .build(), p -> new GrantsMenu(target, !actives).openMenu(p)));

        return toReturn;
    }

    static class GrantButton extends Button {

        final IGrant grant;
        final Profile profile;
        final boolean actives;

        public GrantButton(IGrant grant, Profile profile, boolean actives) {
            this.grant = grant;
            this.profile = profile;
            this.actives = actives;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            API api = Holiday.getInstance().getApi();
            Profile issuer = api.getProfile(grant.getIssuedBy());

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            if (grant.isActive()) {
                item.displayname("&a(Active) " + TimeUtil.formatDate(grant.getIssuedAt()));
                item.lore(
                        CC.MENU_BAR,
                        CC.CHAT + "Rank: " + CC.PRIMARY + grant.getRank().getDisplayName(),
                        CC.CHAT + "Duration: " + CC.PRIMARY + TimeUtil.getDuration(grant.getDuration()),
                        CC.MENU_BAR,
                        CC.CHAT + "Issued By: " + CC.PRIMARY + UserConstants.getNameWithColor(issuer),
                        CC.CHAT + "Issued On: " + CC.PRIMARY + grant.getIssuedOn(),
                        CC.CHAT + "Issued Reason: " + CC.PRIMARY + grant.getReason(),
                        CC.MENU_BAR
                );
            } else {
                Profile remover = api.getProfile(grant.getRevokedBy());
                item.displayname("&c(Inactive) " + TimeUtil.formatDate(grant.getIssuedAt()));
                item.lore("&c                  " + TimeUtil.formatDate(grant.getRevokedAt()),
                        CC.MENU_BAR,
                        CC.CHAT + "Rank: " + CC.PRIMARY + grant.getRank().getDisplayName(),
                        CC.CHAT + "Duration: " + CC.PRIMARY + TimeUtil.getDuration(grant.getDuration()),
                        CC.MENU_BAR,
                        CC.CHAT + "Issued By: " + CC.PRIMARY + UserConstants.getNameWithColor(issuer),
                        CC.CHAT + "Issued On: " + CC.PRIMARY + grant.getIssuedOn(),
                        CC.CHAT + "Issued Reason: " + CC.PRIMARY + grant.getReason(),
                        CC.MENU_BAR,
                        CC.CHAT + "Removed By: " + CC.PRIMARY + UserConstants.getNameWithColor(remover),
                        CC.CHAT + "Removed On: " + CC.PRIMARY + grant.getRevokedOn(),
                        CC.CHAT + "Removed Reason: " + CC.PRIMARY + grant.getRevokeReason(),
                        CC.MENU_BAR
                );
            }

            item.damage(grant.isActive() ? 5 : 14);

            if (grant.isActive() && p0.hasPermission(Perms.GRANTS_EDIT.get()) && !grant.getRank().isDefault()) {
                item.lore(
                        "&cClick to remove this grant.",
                        CC.MENU_BAR
                );
            }
            return item.build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (!grant.isActive()) return;
            if (!player.hasPermission("holiday.grants.edit")) return;
            if (grant.getRank().isDefault()) return;
            Grant g = (Grant) grant;
            g.setActive(false);
            g.revoke(player.getUniqueId(), Holiday.getInstance().getThisServer().getServerName(), "Removed");
            Holiday.getInstance().getApi().saveGrant(g);
            new GrantsMenu(profile, actives).openMenu(player);
        }
    }

}
