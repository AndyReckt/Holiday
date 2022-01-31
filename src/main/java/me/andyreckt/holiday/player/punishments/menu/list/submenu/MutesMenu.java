package me.andyreckt.holiday.player.punishments.menu.list.submenu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.menu.list.button.PunishmentListButton;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PunishmentUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MutesMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return CC.RED + "Mutes";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        int i = 0;

        for (PunishData data : PunishmentUtils.activesMutes()) {
            toReturn.put(i++, new PunishmentListButton(data));
        }

        return toReturn;
    }
}
