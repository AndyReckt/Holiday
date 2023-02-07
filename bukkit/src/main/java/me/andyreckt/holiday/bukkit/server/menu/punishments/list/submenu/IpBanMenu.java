package me.andyreckt.holiday.bukkit.server.menu.punishments.list.submenu;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.list.PunishmentListMenu;
import me.andyreckt.holiday.bukkit.server.menu.punishments.list.button.PunishmentListButton;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.andyreckt.holiday.bukkit.util.text.CC;

public class IpBanMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return CC.RED + "Ip-Bans";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        int i = 0;

        for (IPunishment data : actives()) {
            toReturn.put(i++, new PunishmentListButton(data));
        }

        return toReturn;
    }

    private List<IPunishment> actives() {
        return Holiday.getInstance().getApi().getPunishments().stream()
                .filter(IPunishment::isActive)
                .filter(punishment -> punishment.getType() == IPunishment.PunishmentType.IP_BAN)
                .collect(Collectors.toList());
    }

    @Override
    public Menu backButton() {
        return new PunishmentListMenu();
    }
}