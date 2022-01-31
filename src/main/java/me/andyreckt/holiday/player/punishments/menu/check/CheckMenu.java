package me.andyreckt.holiday.player.punishments.menu.check;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.buttons.EasyButton;
import lombok.NonNull;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.menu.check.submenu.BanMenu;
import me.andyreckt.holiday.player.punishments.menu.check.submenu.BlacklistMenu;
import me.andyreckt.holiday.player.punishments.menu.check.submenu.IpBanMenu;
import me.andyreckt.holiday.player.punishments.menu.check.submenu.MutesMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.PunishmentUtils;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

import java.util.*;

public class CheckMenu extends Menu {
    Profile punished;

    public CheckMenu(@NonNull Profile punished) {
        this.punished = punished;
    }

    @Override
    public Map<Integer, Button> getButtons(@NonNull Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        String playerName = punished.getName();
        List<String> lore = new ArrayList<>();
        for (String alt : punished.formatAlts()) {
            lore.add(CC.translate("&7▎ " + alt));
        }
        toReturn.put(13, new DisplayButton(new ItemBuilder(Material.SKULL_ITEM).durability((short) SkullType.PLAYER.ordinal()).lore(lore).owner(playerName).displayname(CC.RED + "Alt(s) of " + playerName).build()));
        int mutesAmount = PunishmentUtils.mutes(punished).size();
        toReturn.put(10, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 4).displayname("&6Mutes").lore(Arrays.asList("&eThere is a total of &d" + mutesAmount + " &7mutes")).build(), event -> new MutesMenu(punished).open(event)));
        int bansAmount = PunishmentUtils.bans(punished).size();
        toReturn.put(12, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 14).displayname("&cBans").lore(Arrays.asList("&eThere is a total of &d" + bansAmount + " &7ban")).build(), event -> new BanMenu(punished).open(event)));
        int ipBansAmount = PunishmentUtils.ipbans(punished).size();
        toReturn.put(14, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 10).displayname("&cIP-Ban").lore(Arrays.asList("&eThere is a total of &d" + ipBansAmount + " &7ip-ban")).build(), event -> new IpBanMenu(punished).open(event)));
        int blacklistAmounts = PunishmentUtils.blacklists(punished).size();
        toReturn.put(16, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 15).displayname("&4Blacklists").lore(Arrays.asList("&eThere is a total of &d" + blacklistAmounts + " &7blacklist")).build(), event -> new BlacklistMenu(punished).open(event)));

        for (int i = 0; i < 27; i++) {
            Button item = toReturn.get(i);
            if (item == null)
                toReturn.put(i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).displayname(" ").build()));
        }

        return toReturn;
    }

    @Override
    public String getTitle(@NonNull Player player) {
        return CC.RED + "Punish Menu";
    }
}
