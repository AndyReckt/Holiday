package me.andyreckt.holiday.server.menu;


import io.github.damt.menu.Menu;
import io.github.damt.menu.MenuUpdateType;
import io.github.damt.menu.buttons.Button;
import io.github.damt.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.server.menu.button.ServerButton;
import me.andyreckt.holiday.utils.CC;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ServersMenu extends Menu {


    public ServersMenu(Player player) {
        super(player, CC.B_PRIMARY + "Servers", 18);
        this.setUpdateType(MenuUpdateType.RUNNABLE);
    }

    @Override
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> toReturn = new HashMap<>();

        int i = 0;
        for (Server serverData : Holiday.getInstance().getServerHandler().getServers().values()) {
            toReturn.put(i, new ServerButton(serverData));
            i++;
        }

        return toReturn;
    }


}
