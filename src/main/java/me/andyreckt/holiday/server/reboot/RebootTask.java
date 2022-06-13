package me.andyreckt.holiday.server.reboot;

import lombok.Getter;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.Utilities;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
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
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();
        time = millis;
        active = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        runnable = runnable();

        Bukkit.broadcastMessage(messages.getString("REBOOT.REBOOTIN").replace("<time>", TimeUtil.getDuration(time)));

        executor.scheduleAtFixedRate(runnable, 0, 250, TimeUnit.MILLISECONDS);
        Holiday.getInstance().setRebootTask(this);

    }


    public Runnable runnable() {
        return () -> {
        time -= 250;

        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        String fallbackServer = Holiday.getInstance().getSettings().getString("SERVER.FALLBACKSERVERNAME");
        String serverRebootIn = messages.getString("REBOOT.REBOOTIN");
        String restart = messages.getString("REBOOT.RESTART");

        if (time == 0) {
            Bukkit.broadcastMessage(CC.translate(restart));
            Bukkit.getOnlinePlayers().forEach(player -> Utilities.sendToServer(player, fallbackServer));
            Bukkit.getServer().shutdown();
            active = false;
            cancel();
        }

        if (time == 2000) {
            Bukkit.getOnlinePlayers().forEach(player -> Utilities.sendToServer(player, fallbackServer));
        }

        if (rebootTimes.contains(time))
            Bukkit.broadcastMessage(CC.translate(serverRebootIn.replace("<time>", TimeUtil.getDuration(time))));
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
