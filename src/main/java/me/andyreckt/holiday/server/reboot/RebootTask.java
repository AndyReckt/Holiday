package me.andyreckt.holiday.server.reboot;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.Utilities;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reboot extends BukkitRunnable {

    public static long time;
    public static Reboot currentReboot;
    public static boolean active;

    List<Long> rebootTimes = new ArrayList<>(Arrays.asList(10000L, 15000L, 30000L, 60000L, 120000L, 300000L, 600000L, 1200000L, 1800000L, 3600000L));

    public RebootTask(long millis) {

        time = millis;
        active = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        runnable = runnable();

        Bukkit.broadcastMessage(CC.translate("&eThe server will reboot in: &d" + TimeUtil.formatDuration(time)));
        this.runTaskTimerAsynchronously(Holiday.getInstance(), 0, 20L);

    }


    public Runnable runnable() {
        return () -> {
        time -= 100;

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        String fallbackServer = Holiday.getInstance().getSettings().getString("SERVER.FALLBACKSERVERNAME");
        String serverRebootIn = messages.getString("REBOOT.REBOOTIN");
        String restart = messages.getString("REBOOT.RESTART");

        if (time == 0) {
            Bukkit.broadcastMessage(CC.translate("&4Server is rebooting."));
            Bukkit.getOnlinePlayers().forEach(player -> Utilities.sendToServer(player, "Hub-1"));
            Bukkit.getServer().shutdown();
            active = false;
            cancel();
        }

        if (time == 2000) {
            Bukkit.getOnlinePlayers().forEach(player -> Utilities.sendToServer(player, fallbackServer));
        }

        if (rebootTimes.contains(time))
            Bukkit.broadcastMessage(CC.translate(serverRebootIn.replace("<time>", TimeUtil.formatDuration(time))));
        };
    }

    public void cancel() {
        if (this.runnable == null) return;
        this.time = 0;
        this.executor.shutdown();
        this.runnable = null;
        Holiday.getInstance().setRebootTask(null);
    }


}
