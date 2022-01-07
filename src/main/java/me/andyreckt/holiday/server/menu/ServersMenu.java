package me.andyreckt.holiday.server.menu;


import cc.teamfight.astria.Astria;
import cc.teamfight.astria.server.Server;
import cc.teamfight.astria.server.menu.button.ServerButton;
import cc.teamfight.astria.utils.CC;
import com.ericstolly.menu.button.MenuButton;
import com.ericstolly.menu.menu.Menu;
import com.ericstolly.menu.menu.type.MenuType;
import com.ericstolly.menu.menu.update.MenuUpdateType;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ServersMenu extends Menu {


    public ServersMenu() {
        super(Astria.getInstance());
    }

    @Override
    public Map<Integer, MenuButton> getButtons(@NonNull Player player) {
        Map<Integer, MenuButton> toReturn = new HashMap<>();

        int i = 0;
        for (Server.ServerData serverData : Server.servers.values()) {
            toReturn.put(i, new ServerButton(serverData));
            i++;
        }

        return toReturn;
    }

    @Override
    public String getTitle(@NonNull Player player) {
        return CC.B_PRIMARY + "Servers";
    }

    @Override
    public MenuType getMenuType() {
        return MenuType.AUTOMATIC_ROW_CHEST;
    }

    @Override
    public MenuUpdateType getUpdateType() {
        return MenuUpdateType.RUNNABLE;
    }
}
