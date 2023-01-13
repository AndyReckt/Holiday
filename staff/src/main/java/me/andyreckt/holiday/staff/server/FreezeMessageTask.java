package me.andyreckt.holiday.staff.server;

import me.andyreckt.holiday.staff.Staff;
import me.andyreckt.holiday.staff.util.files.SLocale;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeMessageTask extends BukkitRunnable {

    public FreezeMessageTask() {
        this.runTaskTimerAsynchronously(Staff.getInstance(), 20L, 80L);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasMetadata("frozen"))
                .forEach(player -> {
                    for (String message : SLocale.FREEZE_RECURRENT_MESSAGE.getStringListNetwork()) {
                        player.sendMessage(message);
                    }
                });
    }
}
