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
                toReturn.put(toReturn.size(), new GrantButton(grant, target, true));
            }
        } else {
            for (IGrant grant : target.getGrants()) {
                toReturn.put(toReturn.size(), new GrantButton(grant, target, false));
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

        final Grant grant;
        final Profile profile;
        final boolean actives;

        public GrantButton(IGrant grant, Profile profile, boolean actives) {
            this.grant = (Grant) grant;
            this.profile = profile;
            this.actives = actives;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            API api = Holiday.getInstance().getApi();
            Profile issuer = api.getProfile(grant.getIssuedBy());
            Profile target = api.getProfile(grant.getUser());

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            if (grant.isActive()) {
                item.displayname("&a(Active) " + TimeUtil.formatDate(grant.getIssuedAt()));
                item.lore(
                        " ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Rank: " + CC.SECONDARY + grant.getRank().getDisplayName(),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Target: " + CC.SECONDARY + UserConstants.getNameWithColor(target),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Duration: " + CC.SECONDARY + grant.getDurationObject().getFormatted(),
                        " ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued By: " + CC.SECONDARY + UserConstants.getNameWithColor(issuer),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued On: " + CC.SECONDARY + grant.getIssuedOn(),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued Reason: " + CC.SECONDARY + grant.getReason()
                );
            } else {
                Profile remover = api.getProfile(grant.getRevokedBy());
                item.displayname("&c(Inactive) " + TimeUtil.formatDate(grant.getIssuedAt()));
                item.lore("&c                  " + TimeUtil.formatDate(grant.getRevokedAt()),
                        " ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Rank: " + CC.SECONDARY + grant.getRank().getDisplayName(),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Target: " + CC.SECONDARY + UserConstants.getNameWithColor(target),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Duration: " + CC.SECONDARY + grant.getDurationObject().getFormatted(),
                        " ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued By: " + CC.SECONDARY + UserConstants.getNameWithColor(issuer),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued On: " + CC.SECONDARY + grant.getIssuedOn(),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Issued Reason: " + CC.SECONDARY + grant.getReason(),
                        " ",
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Removed By: " + CC.SECONDARY + UserConstants.getNameWithColor(remover),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Removed On: " + CC.SECONDARY + grant.getRevokedOn(),
                        CC.B_PRIMARY + CC.LINE + " " + CC.CHAT + "Removed Reason: " + CC.SECONDARY + grant.getRevokeReason()
                );
            }

            item.damage(grant.isActive() ? 5 : 14);

            if (grant.isActive() && p0.hasPermission(Perms.GRANTS_EDIT.get()) && !grant.getRank().isDefault()) {
                item.lore("", "&cClick to remove this grant.");
            }
            return item.build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (!grant.isActive()) return;
            if (!player.hasPermission(Perms.GRANTS_EDIT.get())) return;
            if (grant.getRank().isDefault()) return;
            Grant g = (Grant) grant;
            g.setActive(false);
            g.revoke(player.getUniqueId(), Holiday.getInstance().getThisServer().getServerName(), "Removed");
            Holiday.getInstance().getApi().saveGrant(g);
            new GrantsMenu(profile, actives).openMenu(player);
        }
    }

}
