package me.andyreckt.holiday.player.staff.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.staff.menu.button.StaffButton;
import me.andyreckt.holiday.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StaffListMenu extends PaginatedMenu {


    @Override
    public String getPrePaginatedTitle(Player p0) {
        return CC.PINK + "Staff List";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> toReturn = new HashMap<>();
        int i = 0;

        for (Profile profile : Holiday.getInstance().getProfileHandler().getOnlineProfiles()) {
            if (!profile.isInStaffMode()) continue;
            toReturn.put(i++, new StaffButton(profile));
        }

        return toReturn;
    }
}
