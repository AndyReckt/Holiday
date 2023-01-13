package me.andyreckt.holiday.bungee.tasks;

import me.andyreckt.holiday.bungee.Bungee;

import java.util.concurrent.TimeUnit;

public class RefreshTask {

    public RefreshTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), () -> {
            Bungee.getInstance().getApi().refreshGrants();
            Bungee.getInstance().getApi().refreshPunishments();
        }, 0, 2, TimeUnit.SECONDS);
    }

}
