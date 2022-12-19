package me.andyreckt.holiday.bungee.tasks;

import me.andyreckt.holiday.bungee.Bungee;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class GrantsUpdateTask {

    public GrantsUpdateTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(),
                () -> Bungee.getInstance().getApi().refreshGrants(), 0, 1, TimeUnit.MINUTES);
    }

}
