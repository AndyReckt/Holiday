package me.andyreckt.holiday.other;

import com.lunarclient.bukkitapi.LunarClientAPI;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.staff.StaffHandler;
import me.andyreckt.holiday.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LunarNametagsTask extends BukkitRunnable {

    public LunarNametagsTask(Holiday plugin) {
        if (plugin.isLunarEnabled() && plugin.getSettings().getBoolean("LUNAR.NAMETAGS")) {
            this.runTaskTimer(plugin, 20L, 20L);
        }
    }

    @Override
    public void run() {
        StaffHandler.getStaffs().keySet().forEach(uid -> {
            if (Bukkit.getPlayer(uid) != null) {
                Player player = Bukkit.getPlayer(uid);

                List<String> ntag = new ArrayList<>();
                ntag.add(CC.translate("&7[Mod Mode]"));
                ntag.add(CC.translate(Holiday.getInstance().getProfileHandler().getByUUID(uid).getDisplayNameWithColorAndVanish()));
                Bukkit.getOnlinePlayers().forEach(target -> {
                    if (LunarClientAPI.getInstance().isRunningLunarClient(target)) LunarClientAPI.getInstance().overrideNametag(player, ntag, target);
                });
            }
        });
    }
}
