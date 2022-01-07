package me.andyreckt.holiday.server.reboot;

import cc.teamfight.astria.Astria;
import cc.teamfight.astria.utils.CC;
import cc.teamfight.astria.utils.TimeUtil;
import cc.teamfight.astria.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reboot extends BukkitRunnable {

    public static long time;
    public static Reboot currentReboot;
    public static boolean active;

    List<Long> rebootTimes = new ArrayList<>(Arrays.asList(10000L, 15000L, 30000L, 60000L, 120000L, 300000L, 600000L, 1200000L, 1800000L, 3600000L));

    public Reboot(long millis) {

        time = millis;
        active = true;
        currentReboot = this;
        Bukkit.broadcastMessage(CC.translate("&eThe server will reboot in: &d" + TimeUtil.formatDuration(time)));
        this.runTaskTimerAsynchronously(Astria.getInstance(), 0, 20L);

    }

    @Override
    public void run() {
        time-= 1000;

        if (time == 0) {
            Bukkit.broadcastMessage(CC.translate("&4Server is rebooting."));
            Bukkit.getOnlinePlayers().forEach(player -> Utilities.sendToServer(player, "Hub-1"));
            Bukkit.getServer().shutdown();
            active = false;
            cancel();
        }

        if (time == 2000) {
            Bukkit.getOnlinePlayers().forEach(player -> Utilities.sendToServer(player, "Hub-1"));
        }

        if (rebootTimes.contains(time)) Bukkit.broadcastMessage(CC.translate("&eThe server will reboot in: &d" + TimeUtil.formatDuration(time)));
    }


}
