package me.andyreckt.holiday.server.menu;


import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.server.menu.button.ServerButton;
import me.andyreckt.holiday.utils.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ServersMenu extends Menu {


    public ServersMenu() {
        this.setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();

        int i = 0;
        for (Server serverData : Holiday.getInstance().getServerHandler().getServers().values()) {
            toReturn.put(i, new ServerButton(serverData));
            i++;
        }

        return toReturn;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return CC.B_PRIMARY + "Servers";
    }


}
