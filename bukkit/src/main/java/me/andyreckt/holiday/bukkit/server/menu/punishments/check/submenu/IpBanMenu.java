package me.andyreckt.holiday.bukkit.server.menu.punishments.check.submenu;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.PunishmentCheckMenu;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.button.PunishmentCheckButton;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import lombok.NonNull;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IpBanMenu extends PaginatedMenu {
    Profile punished;

    public IpBanMenu(Profile punished) {
        this.punished = punished;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(@NonNull Player player) {
        HashMap<Integer, Button> toReturn = new HashMap<>();
        int i = 0;
        for (IPunishment data : ipbans(punished)) {
            toReturn.put(i++, new PunishmentCheckButton(data));
        }
        return toReturn;
    }

    @Override
    public String getPrePaginatedTitle(@NonNull Player player) {
        return CC.RED + "IP-Bans » " + punished.getName();
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

    @Override
    public Menu backButton() {
        return new PunishmentCheckMenu(punished);
    }
}
