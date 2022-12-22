package me.andyreckt.holiday.bukkit.server.menu.punishments.list.submenu;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.menu.punishments.list.button.PunishmentListButton;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;

import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BanMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return CC.RED + "Bans";
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
                .filter(punishment -> punishment.getType() == IPunishment.PunishmentType.BAN)
                .collect(Collectors.toList());
    }
}