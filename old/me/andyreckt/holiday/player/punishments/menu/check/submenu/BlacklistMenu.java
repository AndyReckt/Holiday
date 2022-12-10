package me.andyreckt.holiday.player.punishments.menu.check.submenu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import lombok.NonNull;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.punishments.PunishData;
import me.andyreckt.holiday.player.punishments.menu.check.button.PunishmentCheckButton;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PunishmentUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BlacklistMenu extends PaginatedMenu {
    Profile punished;

    public BlacklistMenu(Profile punished) {
        this.punished = punished;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(@NonNull Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        int i = 0;
        for (PunishData data : PunishmentUtils.blacklists(punished)) {
            toReturn.put(i++, new PunishmentCheckButton(data));
        }
        return toReturn;
    }

    @Override
    public String getPrePaginatedTitle(@NonNull Player player) {
        return CC.RED + "Blacklists » " + punished.getName();
    }
}
