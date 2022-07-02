package me.andyreckt.holiday.player.punishments.menu.list;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.buttons.EasyButton;
import lombok.NonNull;
import me.andyreckt.holiday.player.punishments.menu.list.submenu.BanMenu;
import me.andyreckt.holiday.player.punishments.menu.list.submenu.BlacklistMenu;
import me.andyreckt.holiday.player.punishments.menu.list.submenu.IpBanMenu;
import me.andyreckt.holiday.player.punishments.menu.list.submenu.MutesMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.PunishmentUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ListMenu extends Menu {

    @Override
    public Map<Integer, Button> getButtons(@NonNull Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        int mutesAmount = PunishmentUtils.activesMutes().size();
        toReturn.put(10, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 4).displayname("&6Mutes").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + mutesAmount + " &7mutes").build(), event -> new MutesMenu().open(player)));
        int bansAmount = PunishmentUtils.activesBans().size();
        toReturn.put(12, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 14).displayname("&cBans").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + bansAmount + " &7ban").build(), event -> new BanMenu().open(player)));
        int ipBansAmount = PunishmentUtils.activesIpBans().size();
        toReturn.put(14, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 10).displayname("&cIP-Bans").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + ipBansAmount + " &7ip-ban").build(), event -> new IpBanMenu().open(player)));
        int blacklistAmounts = PunishmentUtils.activesBlacklists().size();
        toReturn.put(16, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 15).displayname("&4Blacklists").lore(CC.CHAT + "There is a total of " + CC.PRIMARY + blacklistAmounts + " &7blacklist").build(), event -> new BlacklistMenu().open(player)));

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

}
