package me.andyreckt.holiday.bukkit.server.menu.punishments.list;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.list.submenu.*;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.DisplayButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import lombok.NonNull;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;

import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PunishmentListMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(@NonNull Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        int mutesAmount = actives(IPunishment.PunishmentType.MUTE).size();
        toReturn.put(10, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 4).displayname("&6Mutes").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + mutesAmount + CC.CHAT + " mutes").build(), event -> new MutesMenu().openMenu(player)));
        int bansAmount = actives(IPunishment.PunishmentType.BAN).size();
        toReturn.put(12, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 14).displayname("&cBans").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + bansAmount + CC.CHAT + " bans").build(), event -> new BanMenu().openMenu(player)));
        int ipBansAmount = actives(IPunishment.PunishmentType.IP_BAN).size();
        toReturn.put(14, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 10).displayname("&cIP-Bans").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + ipBansAmount + CC.CHAT + " ip-bans").build(), event -> new IpBanMenu().openMenu(player)));
        int blacklistAmounts = actives(IPunishment.PunishmentType.BLACKLIST).size();
        toReturn.put(16, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 15).displayname("&4Blacklists").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + blacklistAmounts + CC.CHAT + " blacklists").build(), event -> new BlacklistMenu().openMenu(player)));

        for (int i = 0; i < 27; i++) {
            Button item = toReturn.get(i);
            if (item == null)
                toReturn.put(i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).displayname(" ").build()));
        }

        return toReturn;
    }

    @Override
    public String getTitle(@NonNull Player player) {
        return CC.YELLOW + "Punishment List";
    }

    private List<IPunishment> actives(IPunishment.PunishmentType type) {
        return Holiday.getInstance().getApi().getPunishments().stream()
                .filter(IPunishment::isActive)
                .filter(punishment -> punishment.getType() == type)
                .collect(Collectors.toList());
    }
}
