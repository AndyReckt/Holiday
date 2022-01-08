package me.andyreckt.holiday.listeners;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.StaffSwitchServer;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.ProfileHandler;
import me.andyreckt.holiday.player.disguise.DisguiseHandler;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.player.rank.RankHandler;
import me.andyreckt.holiday.utils.*;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;


public class ProfileListener implements Listener {

    @EventHandler
    public void onPreLogin(PlayerLoginEvent event) {

        ProfileHandler ph = Holiday.getInstance().getProfileHandler();

        if (ph.hasProfile(event.getPlayer().getUniqueId())) {
            Profile oldprofile = ph.getByUUID(event.getPlayer().getUniqueId(), false);
            String newIp = StringUtils.hash(event.getAddress().getHostAddress());
            String newName = event.getPlayer().getName();
            if (!newIp.equals(oldprofile.getIp())) {
                oldprofile.setIp(newIp);
                oldprofile.getIps().add(newIp);
            }
            if (!newName.equals(oldprofile.getName())) {
                oldprofile.setName(newName);
                oldprofile.setLowerCaseName(newName.toLowerCase());
            }
            oldprofile.save();
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        Holiday.getInstance().getProfileHandler().getByUUID(event.getUniqueId());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {

        event.setJoinMessage(null);

        Holiday.getInstance().getExecutor().execute(() -> {

            ProfileHandler ph = Holiday.getInstance().getProfileHandler();
            Profile p = ph.getByPlayer(event.getPlayer());
            RankHandler rh = Holiday.getInstance().getRankHandler();

            BasicConfigurationFile config = Holiday.getInstance().getSettings();

            if (config.getBoolean("NAMEMC.ENABLED")) {
                if (!p.isLiked()) {
                    if (PlayerUtil.hasVotedOnNameMC(p.getUuid())) {
                        p.setLiked(true);
                        p.getPlayer().sendMessage(CC.translate(config.getString("NAMEMC.MESSAGE")));
                        if (config.getBoolean("NAMEMC.RANK.ENABLED")) {
                            if (p.getHighestRank() == rh.getDefaultRank()) {
                                Grant grant = new Grant(p.getUuid(),
                                        ph.getConsoleProfile().getUuid(),
                                        rh.getFromName(config.getString("NAMEMC.RANK.NAME")),
                                        TimeUtil.PERMANENT);
                                grant.save();
                            }
                        }
                    }
                } else {
                    if (!PlayerUtil.hasVotedOnNameMC(p.getUuid())) {
                        p.setLiked(false);

                        for (Grant o : p.getActiveGrants()) {
                            if (!(o.getRank().getName().equalsIgnoreCase(config.getString("NAMEMC.RANK.NAME")))) continue;
                            o.setActive(false);
                            o.save();
                        }
                    }
                }
            }

            if (p.isDisguisedOnLogin() || p.isDisguised()) {
                DisguiseHandler.DisguiseData data = Holiday.getInstance().getDisguiseHandler().getDisguiseData(p.getUuid());
                Tasks.run(() -> {
                    try {
                        Holiday.getInstance().getDisguiseHandler().disguise(event.getPlayer(), data.disguiseRank(), data.skinName(), data.displayName(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            Tasks.runAsyncLater(() -> {
                p.setCurrentServer(config.getString("SERVER.NAME"));
                p.setOnline(true);

                if (p.isStaff()) new StaffSwitchServer(p, false);

                p.save();
            }, 10L);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Holiday.getInstance().getExecutor().execute(() -> {
            Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(event.getPlayer());
            try {
                if (profile.isStaff()) {
                    profile.setCurrentServer(Holiday.getInstance().getSettings().getString("SERVER.NAME"));
                    new StaffSwitchServer(profile, true);
                }
            } catch (Exception ignored) {
            } finally {
                profile.setLastSeen(System.currentTimeMillis());
                profile.setCurrentServer(null);
                profile.setOnline(false);
                profile.save();

                Holiday.getInstance().getProfileHandler().removeFromCache(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
        Holiday.getInstance().getExecutor().execute(() -> {
            Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(event.getPlayer());
            try {
                if (profile.isStaff()) {
                    profile.setCurrentServer(Holiday.getInstance().getSettings().getString("SERVER.NAME"));
                    new StaffSwitchServer(profile, true);
                }
            } catch (Exception ignored) {
            } finally {
                profile.setLastSeen(System.currentTimeMillis());
                profile.setCurrentServer(null);
                profile.setOnline(false);
                profile.save();

                Holiday.getInstance().getProfileHandler().removeFromCache(event.getPlayer().getUniqueId());
            }
        });
    }


}
