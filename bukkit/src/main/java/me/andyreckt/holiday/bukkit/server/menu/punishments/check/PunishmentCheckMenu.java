package me.andyreckt.holiday.bukkit.server.menu.punishments.check;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.submenu.BanMenu;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.submenu.BlacklistMenu;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.submenu.IpBanMenu;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.submenu.MutesMenu;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.DisplayButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import lombok.NonNull;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

import java.util.*;

public class PunishmentCheckMenu extends Menu {
    Profile punished;

    public PunishmentCheckMenu(@NonNull Profile punished) {
        this.punished = punished;
    }

    @Override
    public Map<Integer, Button> getButtons(@NonNull Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        String playerName = punished.getName();
        List<String> lore = new ArrayList<>();
        for (String alt : punished.getAltsFormatted()) {
            lore.add(CC.translate("&7▎ " + alt));
        }
        toReturn.put(13, new DisplayButton(new ItemBuilder(Material.SKULL_ITEM).durability((short) SkullType.PLAYER.ordinal()).lore(lore).owner(playerName).displayname(CC.RED + "Alt(s) of " + playerName).build()));
        int mutesAmount = mutes(punished).size();
        toReturn.put(10, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 4).displayname("&6Mutes").lore(Arrays.asList(CC.CHAT + "There is a total of " + CC.PRIMARY + mutesAmount + CC.CHAT + " mutes")).build(), event -> new MutesMenu(punished).openMenu(event)));
        int bansAmount = bans(punished).size();
        toReturn.put(12, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 14).displayname("&cBans").lore(Arrays.asList(CC.CHAT + "There is a total of " + CC.PRIMARY + bansAmount + CC.CHAT + " bans")).build(), event -> new BanMenu(punished).openMenu(event)));
        int ipBansAmount = ipbans(punished).size();
        toReturn.put(14, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 10).displayname("&cIP-Ban").lore(Arrays.asList(CC.CHAT + "There is a total of " + CC.PRIMARY + ipBansAmount + CC.CHAT + " ip-bans")).build(), event -> new IpBanMenu(punished).openMenu(event)));
        int blacklistAmounts = blacklists(punished).size();
        toReturn.put(16, new EasyButton(
                new ItemBuilder(Material.WOOL).durability((short) 15).displayname("&4Blacklists").lore(Arrays.asList(CC.CHAT + "There is a total of " + CC.PRIMARY + blacklistAmounts + CC.CHAT + " blacklists")).build(), event -> new BlacklistMenu(punished).openMenu(event)));

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

    private List<IPunishment> mutes(Profile profile) {
        List<IPunishment> toReturn = new ArrayList<>();
        for (IPunishment punishment : profile.getPunishments()) {
            if (punishment.getType().equals(IPunishment.PunishmentType.MUTE)) {
                toReturn.add(punishment);
            }
        }
        return toReturn;
    }

    private List<IPunishment> bans(Profile profile) {
        List<IPunishment> toReturn = new ArrayList<>();
        for (IPunishment punishment : profile.getPunishments()) {
            if (punishment.getType().equals(IPunishment.PunishmentType.BAN)) {
                toReturn.add(punishment);
            }
        }
        return toReturn;
    }

    private List<IPunishment> ipbans(Profile profile) {
        List<IPunishment> toReturn = new ArrayList<>();
        for (IPunishment punishment : profile.getPunishments()) {
            if (punishment.getType().equals(IPunishment.PunishmentType.IP_BAN)) {
                toReturn.add(punishment);
            }
        }
        return toReturn;
    }

    private List<IPunishment> blacklists(Profile profile) {
        List<IPunishment> toReturn = new ArrayList<>();
        for (IPunishment punishment : profile.getPunishments()) {
            if (punishment.getType().equals(IPunishment.PunishmentType.BLACKLIST)) {
                toReturn.add(punishment);
            }
        }
        return toReturn;
    }
}
