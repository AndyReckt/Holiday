package me.andyreckt.holiday.bukkit.server.tasks;

import lombok.Getter;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.player.PlayerUtils;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class RebootTask {

    private long time;
    private boolean active;

    private Runnable runnable;

    private final List<Long> rebootTimes = new ArrayList<>(Arrays.asList(1000L, 2000L, 3000L, 4000L, 5000L, 10000L, 15000L, 30000L, 60000L, 120000L, 300000L, 600000L, 1200000L, 1800000L, 3600000L));

    private final ScheduledExecutorService executor;

    public RebootTask(long millis) {
        time = millis;
        active = true;
        executor = Holiday.getInstance().getScheduledExecutor();
        runnable = runnable();

        Bukkit.broadcastMessage(Locale.REBOOT_MESSAGE.getString().replace("%time%", Duration.of(time).toRoundedTime()));

        executor.scheduleAtFixedRate(runnable, 0, 250, TimeUnit.MILLISECONDS);
        Holiday.getInstance().setRebootTask(this);
    }


    private Runnable runnable() {
        return () -> {
        time -= 250;

        String fallbackServer = Locale.FALLBACK_SERVER.getString();
        String serverRebootIn = Locale.REBOOT_MESSAGE.getString();
        String restart = Locale.REBOOT_NOW.getString();

        if (time == 0) {
            Bukkit.broadcastMessage(CC.translate(restart));
            Bukkit.getOnlinePlayers().forEach(player -> PlayerUtils.sendToServer(player, fallbackServer));
            Bukkit.getServer().shutdown();
            active = false;
            cancel();
        }

        if (time == 2000) {
            Bukkit.getOnlinePlayers().forEach(player -> PlayerUtils.sendToServer(player, fallbackServer));
        }

        if (rebootTimes.contains(time))
            Bukkit.broadcastMessage(CC.translate(serverRebootIn.replace("%time%", Duration.of(time).toRoundedTime())));

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
