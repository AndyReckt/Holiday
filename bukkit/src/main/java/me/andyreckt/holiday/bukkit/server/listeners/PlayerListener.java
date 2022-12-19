package me.andyreckt.holiday.bukkit.server.listeners;

import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.server.Server;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.redis.pubsub.packets.BroadcastPacket;
import me.andyreckt.holiday.core.util.text.HashUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLoginUserProfile(AsyncPlayerPreLoginEvent event) {
        Profile profile = Holiday.getInstance().getApi().getProfile(event.getUniqueId());

        if (!profile.getName().equalsIgnoreCase(event.getName())) {
            profile.setName(event.getName());
        }

        if (!profile.getIp().equalsIgnoreCase(HashUtils.hash(event.getAddress().getHostAddress()))) {
            profile.addNewCurrentIP(event.getAddress().getHostAddress());
        }

        Holiday.getInstance().getApi().saveProfile(profile);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginPunishments(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        IPunishment punishment = profile.getActivePunishments().stream()
                .filter(pun -> pun.getType() == IPunishment.PunishmentType.BAN ||
                        pun.getType() == IPunishment.PunishmentType.IP_BAN ||
                        pun.getType() == IPunishment.PunishmentType.BLACKLIST)
                .findFirst().orElse(null);

        if (punishment == null) return;
        if (punishment.getType() == IPunishment.PunishmentType.BAN && Locale.BANNED_JOIN.getBoolean()) return;

        Locale locale;
        switch (punishment.getType()) {
            case BAN:
                locale = Locale.PUNISHMENT_BAN_KICK;
                break;
            case IP_BAN:
                locale = Locale.PUNISHMENT_IP_BAN_KICK;
                break;
            case BLACKLIST:
                locale = Locale.PUNISHMENT_BLACKLIST_KICK;
                break;
            default:
                return;
        }

        String kickMessage = locale.getStringNetwork().replace("%reason%", punishment.getAddedReason())
                .replace("%duration%", TimeUtil.getDuration(punishment.getDuration()));
        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        event.setKickMessage(CC.translate(kickMessage));
        String toSend = Locale.PUNISHMENT_BANNED_LOGIN_ALERT.getString()
                .replace("%player%", player.getName());
        Holiday.getInstance().getApi().getRedis().sendPacket(new BroadcastPacket(toSend, Perms.ADMIN_VIEW_NOTIFICATIONS.get()));
        //TODO: broadcast alt login try
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLoginWhitelist(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;

        Server server = Holiday.getInstance().getThisServer();
        if (!server.isWhitelisted()) return;
        IRank rank = server.getWhitelistRank();

        if (profile.getHighestRank().isAboveOrEqual(rank)) return;
        if (server.getWhitelistedPlayers().contains(player.getUniqueId())) return;

        event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        event.setKickMessage(CC.translate(Locale.LOGIN_WHITELIST.getString().replace("%rank%", rank.getDisplayName())));
    }
}
