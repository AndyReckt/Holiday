package me.andyreckt.holiday.bukkit.util.files;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static me.andyreckt.holiday.bukkit.util.files.Locale.FileName.MESSAGES;
import static me.andyreckt.holiday.bukkit.util.files.Locale.FileName.SETTINGS;

@Getter(value = AccessLevel.PRIVATE)
public enum Locale {
    /* MONGO */
    MONGO_HOST(SETTINGS.get(), "mongo.host", "localhost"),
    MONGO_PORT(SETTINGS.get(), "mongo.port", 27017),
    MONGO_AUTH(SETTINGS.get(), "mongo.auth", false),
    MONGO_USERNAME(SETTINGS.get(), "mongo.username", "foo"),
    MONGO_PASSWORD(SETTINGS.get(), "mongo.password", "bar"),
    MONGO_DATABASE(SETTINGS.get(), "mongo.database", "holiday"),

    /* REDIS */
    REDIS_HOST(SETTINGS.get(), "redis.host", "localhost"),
    REDIS_PORT(SETTINGS.get(), "redis.port", 6379),
    REDIS_AUTH(SETTINGS.get(), "redis.auth", false),
    REDIS_PASSWORD(SETTINGS.get(), "redis.password", "bar"),

    /* SERVER */
    SERVER_ID(SETTINGS.get(), "server.bungee-name", "$undefined"),
    SERVER_NAME(SETTINGS.get(), "server.name", "Unknown"),
    USE_CUSTOM_IP(SETTINGS.get(), "server.use-custom-ip", false),
    CUSTOM_IP(SETTINGS.get(), "server.custom-ip", "172.18.0.1"),
    BANNED_JOIN(SETTINGS.get(), "server.banned-join", false),
    FALLBACK_SERVER(SETTINGS.get(), "server.fallback-server-id", "lobby"),
    CHAT_ENABLED(SETTINGS.get(), "server.chat.enabled", true),
    CHAT_FORMAT(SETTINGS.get(), "server.chat.format", "%prefix%%player%%suffix%&7: %message%"),
    LOGIN_WHITELIST(SETTINGS.get(), "server.whitelist-login", "&eThe server is currently &cWhitelisted&e. \\n &eYou need %rank% &eor above to be able to log into the server."),

    DISABLED_COMMANDS(SETTINGS.get(), "server.disabled-commands", "save-all", "save-off", "save-on", "w",
            "bukkit:op", "bukkit:deop", "bukkit:ban", "bukkit:kick", "bukkit:mute", "bukkit:tempmute", "bukkit:unmute",
            "bukkit:tempban", "bukkit:unban", "bukkit:banip", "bukkit:unbanip", "bukkit:tempbanip", "bukkit:banlist",
            "bukkit:pardon", "bukkit:pardonip", "bukkit:whitelist", "bukkit:reload", "bukkit:stop", "bukkit:save-all",
            "bukkit:save-off", "bukkit:save-on", "bukkit:say", "bukkit:tell", "bukkit:me",
            "bukkit:w", "bukkit:broadcast", "bukkit:bc", "bukkit:bcast"),

    /* COLORS */
    COLOR_PRIMARY(SETTINGS.get(), "color.primary", "AQUA"),
    COLOR_SECONDARY(SETTINGS.get(), "color.secondary", "DARK_AQUA"),
    COLOR_CHAT(SETTINGS.get(), "color.chat", "YELLOW"),

    /* FILTERS */
    FILTER_SEND(SETTINGS.get(), "filter.send-to-staff", true),
    FILTER_MESSAGE(SETTINGS.get(), "filter.message", "&d[Filtered] &5[%server%] %player%&e: %message%"),
    FILTER_LOW_ALLOW(SETTINGS.get(), "filter.low.allow-message", true),
    FILTER_LOW_LIST(SETTINGS.get(), "filter.low.list", "cunt", "anal", "beaner", "nazi", "paki"),
    FILTER_HIGH_ALLOW(SETTINGS.get(), "filter.high.allow-message", false),
    FILTER_HIGH_MUTE(SETTINGS.get(), "filter.high.mute", true),
    FILTER_HIGH_MUTE_DURATION(SETTINGS.get(), "filter.high.mute-duration", "3h"),
    FILTER_HIGH_MUTE_REASON(SETTINGS.get(), "filter.high.mute-reason", "Automute (%word%)"),
    FILTER_HIGH_LIST(SETTINGS.get(), "filter.high.list", "nigger", "nigga", "coon", "faggot"),

    /* NETWORK */
    NETWORK_NAME(SETTINGS.get(), "network.name", "example network"),
    NETWORK_IP(SETTINGS.get(), "network.ip", "example.com"),
    NETWORK_WEBSITE(SETTINGS.get(), "network.website", "https://example.com"),
    NETWORK_STORE(SETTINGS.get(), "network.store", "https://store.example.com"),
    NETWORK_DISCORD(SETTINGS.get(), "network.discord", "https://discord.example.com"),
    NETWORK_TWITTER(SETTINGS.get(), "network.twitter", "https://twitter.com/examplenetwork"),
    NETWORK_TEAMSPEAK(SETTINGS.get(), "network.teamspeak", "ts.example.com"),

    /* COOLDOWN */
    REPORT_COOLDOWN(SETTINGS.get(), "cooldowns.report-in-seconds", 300),
    HELPOP_COOLDOWN(SETTINGS.get(), "cooldowns.helpop-in-seconds", 150),

    /* NAMEMC */
    NAMEMC_MESSAGE(SETTINGS.get(), "namemc.liked-message", "&eThanks for liking our namemc!"),
    NAMEMC_RANK_ENABLED(SETTINGS.get(), "namemc.rank.enabled", false),
    NAMEMC_RANK_NAME(SETTINGS.get(), "namemc.rank.name", "Voter"),

    /* SERVER STARTUP */
    SERVER_STARTUP(MESSAGES.get(), "server.startup-alert", "&c[A] &a%server% has just started and is now joinable!"),
    SERVER_SHUTDOWN(MESSAGES.get(), "server.shutdown-alert", "&c[A] %server% just went offline and is no longer joinable!"),

    /* PUNISHMENTS */
    PUNISHMENT_SILENT_PREFIX(MESSAGES.get(), "punishment.silent-prefix", "&7(Silent) "),
    PUNISHMENT_MUTE_MESSAGE(MESSAGES.get(), "punishment.mute.message", "%silent%&c%player% &chas been muted by %executor% &cfor %reason%."),
    PUNISHMENT_MUTE_PLAYER(MESSAGES.get(), "punishment.mute.player", "&cYou are muted, this mute is permanent."),
    PUNISHMENT_TEMP_MUTE_MESSAGE(MESSAGES.get(), "punishment.temp-mute.message", "%silent%&c%player% &chas been muted by %executor% &cfor %reason%."),
    PUNISHMENT_TEMP_MUTE_PLAYER(MESSAGES.get(), "punishment.temp-mute.player", "&cYou are muted, this mute will expire in %duration%."),
    PUNISHMENT_BAN_MESSAGE(MESSAGES.get(), "punishment.ban.message", "%silent%&c%player% &chas been permanently banned by %executor% &cfor %reason%."),
    PUNISHMENT_BAN_KICK(MESSAGES.get(), "punishment.ban.kick-message", "&cYou have been permanently banned from %network_name% for %reason%. \\n &cAppeal at %discord% or buy an unban at %store%."),
    PUNISHMENT_TEMP_BAN_MESSAGE(MESSAGES.get(), "punishment.temp-ban.message", "%silent%&c%player% &chas been temporarily banned by %executor% &cfor %reason% for a duration of %duration%."),
    PUNISHMENT_TEMP_BAN_KICK(MESSAGES.get(), "punishment.temp-ban.kick-message", "&cYou have been temporarily banned from %network_name% for a duration of %duration% for %reason%. \\n &cAppeal at %discord% or buy an unban at %store%."),
    PUNISHMENT_IP_BAN_MESSAGE(MESSAGES.get(), "punishment.ip-ban.message", "%silent%&c%player% &chas been permanently ip-banned by %executor% &cfor %reason%."),
    PUNISHMENT_IP_BAN_KICK(MESSAGES.get(), "punishment.ip-ban.kick-message", "&cYou have been permanently ip-banned from %network_name% for %reason%. \\n &cAppeal at %discord% or buy an unban at %store%."),
    PUNISHMENT_BLACKLIST_MESSAGE(MESSAGES.get(), "punishment.ban.message", "%silent%&c%player% &chas been blacklisted by %executor% &cfor %reason%."),
    PUNISHMENT_BLACKLIST_KICK(MESSAGES.get(), "punishment.blacklist.kick-message", "&cYou have been blacklisted from %network_name% for %reason%. \\n &cThis type of punishment cannot be appealed."),
    PUNISHMENT_BAN_REVOKED(MESSAGES.get(), "punishment.revoked.ban", "%silent%&a%player% &ahas been unbanned by %executor% &afor %reason%."),
    PUNISHMENT_MUTE_REVOKED(MESSAGES.get(), "punishment.revoked.mute", "%silent%&a%player% &ahas been unmuted by %executor% &afor %reason%."),
    PUNISHMENT_BLACKLIST_REVOKED(MESSAGES.get(), "punishment.revoked.blacklist", "%silent%&a%player% &ahas been unblacklisted by %executor% &afor %reason%."),
    PUNISHMENT_BANNED_LOGIN_ALERT(MESSAGES.get(), "punishment.alert.banned", "&c[A] &c%player% tried to join but is banned!"),
    PUNISHMENT_ALT_LOGIN_ALERT(MESSAGES.get(), "punishment.alert.alt", "&c[A] &c%player% might be alting while banned! &7(%alts%)"),

    /* RANKS */
    RANK_SUCCESSFULLY_CREATED(MESSAGES.get(), "rank.successfully-created", "&aSuccessfully created rank %rank%."),
    RANK_SUCCESSFULLY_DELETED(MESSAGES.get(), "rank.successfully-deleted", "&aSuccessfully deleted rank %rank%."),
    RANK_PERMISSION_ADDED(MESSAGES.get(), "rank.permission-added", "&aSuccessfully added permission '%permission%' to rank %rank%."),
    RANK_PERMISSION_REMOVED(MESSAGES.get(), "rank.permission-removed", "&aSuccessfully removed permission '%permission%' from rank %rank%."),
    RANK_INHERITANCE_ADDED(MESSAGES.get(), "rank.inheritance-added", "&aSuccessfully added inheritance '%child%' to rank %rank%."),
    RANK_INHERITANCE_REMOVED(MESSAGES.get(), "rank.inheritance-removed", "&aSuccessfully removed inheritance '%child%' from rank %rank%."),
    RANK_PRIORITY_SET(MESSAGES.get(), "rank.priority-set", "&aSuccessfully set priority of rank %rank% to %priority%."),
    RANK_ENTER_NAME(MESSAGES.get(), "rank.edit-name", "&aPlease enter the new name of the rank."),
    RANK_ENTER_NAME_SUCCESS(MESSAGES.get(), "rank.edit-name-success", "&aSuccessfully changed the name of the rank to %name%."),
    RANK_ENTER_DISPLAY_NAME(MESSAGES.get(), "rank.edit-display-name", "&aPlease enter the new display name of the rank."),
    RANK_ENTER_DISPLAY_NAME_SUCCESS(MESSAGES.get(), "rank.edit-display-name-success", "&aSuccessfully changed the display name of the rank to %name%."),
    RANK_ENTER_PREFIX(MESSAGES.get(), "rank.edit-prefix", "&aPlease enter the new prefix of the rank."),
    RANK_ENTER_PREFIX_SUCCESS(MESSAGES.get(), "rank.edit-prefix-success", "&aSuccessfully changed the prefix of the rank to %prefix%."),
    RANK_ENTER_SUFFIX(MESSAGES.get(), "rank.edit-suffix", "&aPlease enter the new suffix of the rank."),
    RANK_ENTER_SUFFIX_SUCCESS(MESSAGES.get(), "rank.edit-suffix-success", "&aSuccessfully changed the suffix of the rank to %suffix%."),
    RANK_COLOR_UPDATED(MESSAGES.get(), "rank.color-updated", "&aSuccessfully updated the color of the rank to %color%."),

    /* CHAT */
    GLOBAL_CHAT_CLEAR(MESSAGES.get(), "chat.global.chat-clear", "&cThe global chat has been cleared."),
    STAFF_CHAT_CLEAR(MESSAGES.get(), "chat.staff.chat-clear", "&b[S] &3[%server%] &eThe chat has been cleared by %executor%"),
    GLOBAL_CHAT_MUTED(MESSAGES.get(), "chat.global.chat-muted", "&cThe global chat has been muted."),
    STAFF_CHAT_MUTED(MESSAGES.get(), "chat.staff.chat-muted", "&b[S] &3[%server%] &eThe chat has been muted by %executor%"),
    GLOBAL_CHAT_UNMUTED(MESSAGES.get(), "chat.global.chat-unmuted", "&aThe global chat has been unmuted."),
    STAFF_CHAT_UNMUTED(MESSAGES.get(), "chat.staff.chat-unmuted", "&b[S] &3[%server%] &eThe chat has been unmuted by %executor%"),
    GLOBAL_CHAT_SLOWED(MESSAGES.get(), "chat.global.chat-slowed", "&cThe global chat has been slowed to %delay% seconds."),
    STAFF_CHAT_SLOWED(MESSAGES.get(), "chat.staff.chat-slowed", "&b[S] &3[%server%] &eThe chat has been slowed down to %delay% seconds by %executor%"),
    GLOBAL_CHAT_UNSLOWED(MESSAGES.get(), "chat.global.chat-unslowed", "&aThe global chat has been unslowed."),
    STAFF_CHAT_UNSLOWED(MESSAGES.get(), "chat.staff.chat-unslowed", "&b[S] &3[%server%] &eThe chat has been unslowed by %executor%"),

    /* WHITELIST */
    GLOBAL_WHITELIST_ENABLED(MESSAGES.get(), "whitelist.global.whitelist-enabled", "&cThe whitelist has been enabled."),
    STAFF_WHITELIST_ENABLED(MESSAGES.get(), "whitelist.staff.whitelist-enabled", "&b[S] &3[%server%] &eThe whitelist has been enabled by %executor%"),
    GLOBAL_WHITELIST_DISABLED(MESSAGES.get(), "whitelist.global.whitelist-disabled", "&aThe whitelist has been disabled."),
    STAFF_WHITELIST_DISABLED(MESSAGES.get(), "whitelist.staff.whitelist-disabled", "&b[S] &3[%server%] &eThe whitelist has been disabled by %executor%"),
    PLAYER_WHITELIST_RANK(MESSAGES.get(), "whitelist.player.whitelist-rank", "&aYou have successfully updated the whitelist rank to %rank%."),
    STAFF_WHITELIST_RANK(MESSAGES.get(), "whitelist.staff.whitelist-rank", "&b[S] &3[%server%] &eThe whitelist rank has been set to %rank% &eby %executor%"),
    PLAYER_WHITELIST_ADDED(MESSAGES.get(), "whitelist.player.whitelist-added", "&aYou have successfully added %player% &ato the whitelist."),
    PLAYER_WHITELIST_REMOVED(MESSAGES.get(), "whitelist.player.whitelist-removed", "&aYou have successfully removed %player% &afrom the whitelist."),

    /* SERVER MANAGER */
    PLAYER_SERVER_MANAGER_RUN_ALL(MESSAGES.get(), "server-manager.player.run-all", "&aSuccessfully ran command '%command%' on all servers."),
    PLAYER_SERVER_MANAGER_RUN_SERVER(MESSAGES.get(), "server-manager.player.run-server", "&aSuccessfully ran command '%command%' on server %server%."),
    STAFF_SERVER_MANAGER_RUN_ALL(MESSAGES.get(), "server-manager.staff.run-all", "&c[A] &3[%server%] &e%executor% &ehas run command '%command%' on all servers."),
    STAFF_SERVER_MANAGER_RUN_SERVER(MESSAGES.get(), "server-manager.staff.run-server", "&c[A] &3[%server%] &e%executor% &ehas run command '%command%' on server %serverid%."),
    PLAYER_SERVER_MANAGER_INFO(MESSAGES.get(), "server-manager.player.info",
            CC.CHAT_BAR,
            "&bServer Manager",
            "&eServer ID: &3%id%",
            "&eServer Name: &3%name%",
            "&eServer Status: %status%",
            "&ePlayers: &3%players%&7/&3%maxplayers%",
            "&eServer TPS: %tps%",
            CC.CHAT_BAR),
    PLAYER_SERVER_MANAGER_INFO_OFFLINE(MESSAGES.get(), "server-manager.player.info-offline", "&cThe server is currently offline or does not exist."),

    /* GAMEMODE */
    GAMEMODE_UPDATED_SELF(MESSAGES.get(), "gamemode.player.updated-self", "&aSuccessfully updated your gamemode to %gamemode%."),
    GAMEMODE_UPDATED_OTHER(MESSAGES.get(), "gamemode.player.updated-other", "&aSuccessfully updated %player%'s gamemode to %gamemode%."),
    GAMEMODE_UPDATED_TARGET(MESSAGES.get(), "gamemode.player.target-updated", "&aYour gamemode has been updated to %gamemode%."),
    STAFF_GAMEMODE_UPDATED(MESSAGES.get(), "gamemode.staff.updated-other", "&b[S] &3[%server%] &e%executor% &ehas updated %player%'s gamemode to %gamemode%."),
    STAFF_GAMEMODE_UPDATED_SELF(MESSAGES.get(), "gamemode.staff.updated-self", "&b[S] &3[%server%] &e%executor% &ehas updated their gamemode to %gamemode%."),

    /* TELEPORT */
    TELEPORT_PLAYER(MESSAGES.get(), "teleport.player.teleport", "&aSuccessfully teleported to %player%."),
    TELEPORT_PLAYER_HERE(MESSAGES.get(), "teleport.player.teleport-here", "&aSuccessfully teleported %player% to you."),
    TELEPORT_PLAYER_ALL(MESSAGES.get(), "teleport.player.teleport-all", "&aSuccessfully teleported all players to you."),
    TELEPORT_PLAYER_POSITION(MESSAGES.get(), "teleport.player.teleport-position", "&aSuccessfully teleported to %x%, %y%, %z%."),
    TELEPORT_STAFF_PLAYER(MESSAGES.get(), "teleport.staff.teleport", "&b[S] &3[%server%] &e%executor% &ehas teleported to %player%."),
    TELEPORT_STAFF_PLAYER_HERE(MESSAGES.get(), "teleport.staff.teleport-here", "&b[S] &3[%server%] &e%executor% &ehas teleported %player% to them."),
    TELEPORT_STAFF_PLAYER_ALL(MESSAGES.get(), "teleport.staff.teleport-all", "&b[S] &3[%server%] &e%executor% &ehas teleported all players to them."),
    TELEPORT_STAFF_PLAYER_POSITION(MESSAGES.get(), "teleport.staff.teleport-position", "&b[S] &3[%server%] &e%executor% &ehas teleported to %x%, %y%, %z%."),

    /* SOCIAL */
    SOCIAL_DISCORD(MESSAGES.get(), "social.discord", "&aJoin our discord at &3%discord%&a!"),
    SOCIAL_TWITTER(MESSAGES.get(), "social.twitter", "&aFollow us on twitter at &3%twitter%&a!"),
    SOCIAL_TEAMSPEAK(MESSAGES.get(), "social.teamspeak", "&aJoin our teamspeak at &3%teamspeak%&a!"),
    SOCIAL_STORE(MESSAGES.get(), "social.store", "&aCheck out our store at &3%store%&a!"),

    /* SETTINGS */
    SETTINGS_PRIVATE_MESSAGE_ON(MESSAGES.get(), "settings.player.private-message.on", "&aYou have successfully toggled private messages on."),
    SETTINGS_PRIVATE_MESSAGE_OFF(MESSAGES.get(), "settings.player.private-message.off", "&cYou have successfully toggled private messages off."),
    SETTINGS_PRIVATE_MESSAGE_SOUNDS_ON(MESSAGES.get(), "settings.player.private-message-sounds.on", "&aYou have successfully toggled private message sounds on."),
    SETTINGS_PRIVATE_MESSAGE_SOUNDS_OFF(MESSAGES.get(), "settings.player.private-message-sounds.off", "&cYou have successfully toggled private message sounds off."),
    SETTINGS_STAFF_SOCIAL_SPY_ON(MESSAGES.get(), "settings.staff.social-spy.on", "&aYou have successfully toggled social spy on."),
    SETTINGS_STAFF_SOCIAL_SPY_OFF(MESSAGES.get(), "settings.staff.social-spy.off", "&cYou have successfully toggled social spy off."),

    /* CONVERSAION */
    CONVERSATION_FORMAT_SENT(MESSAGES.get(), "conversation.format.sent", "&e(To %player%&e) &f%message%"),
    CONVERSATION_FORMAT_RECEIVED(MESSAGES.get(), "conversation.format.received", "&e(From %player%&e) &f%message%"),
    CONVERSATION_FORMAT_SOCIAL_SPY(MESSAGES.get(), "conversation.format.social-spy", "&7[&cSPY&7] &e(%sender% &7» %target%&e) &7%message%"),

    /* GRANT */
    GRANT_PLAYER(MESSAGES.get(), "grant.message", "&aYou have granted the rank %rank% &ato %player% &afor a duration of %duration%&a."),
    GRANT_TARGET(MESSAGES.get(), "grant.target", "&aYou have been granted the rank %rank% &afor a duration of %duration%&a."),
    GRANT_STAFF(MESSAGES.get(), "grant.staff", "&c[A] &3[%server%] %executor% &ehas granted the rank %rank% &eto %player% &efor a duration of %duration%&e."),

    /* REBOOT */
    REBOOT_STARTED(MESSAGES.get(), "reboot.started", "&aSuccessfully started a reboot task of %time%."),
    REBOOT_MESSAGE(MESSAGES.get(), "reboot.message", "&cThe server is rebooting in %time%."),
    REBOOT_NOW(MESSAGES.get(), "reboot.now", "&cThe server is rebooting now."),
    REBOOT_CANCELLED(MESSAGES.get(), "reboot.cancelled", "&aThe reboot has been cancelled."),

    /* REPORT & HELPOP */
    REPORT_MESSAGE(MESSAGES.get(), "report.message", "&aYour report has been sent to the staff team."),
    HELPOP_MESSAGE(MESSAGES.get(), "helpop.message", "&aYou have successfully sent a request, a staff member will answer you shortly."),
    REPORT_FORMAT(MESSAGES.get(), "report.format", "&9[REPORT] &3[%server%] %player% &9reported %target% %newline%  &7» &9Reason: &3%reason%"),
    REPORT_CLICK_MESSAGE(MESSAGES.get(), "report.click-message", "&eClick to join %server%"),
    HELPOP_FORMAT(MESSAGES.get(), "helpop.format", "&2[HELPOP] &3[%server%] %player% &2needs help %newline%  &7» &aRequest: &a%message%"),
    HELPOP_CLICK_MESSAGE(MESSAGES.get(), "helpop.click-message", "&eClick to answer %player%"),

    /* GENERAL COMMANDS */
    PING(MESSAGES.get(), "ping.self", "&aYour ping is %ping%ms."),
    PING_OTHER(MESSAGES.get(), "ping.other", "&a%player%'s ping is %ping%ms &7(difference: %difference%ms)"),
    RENAME(MESSAGES.get(), "rename", "&aYou have successfully renamed your %item% to &r%name%&a."),
    FLY_ENABLED(MESSAGES.get(), "fly.enabled", "&aYou have successfully enabled fly mode."),
    FLY_DISABLED(MESSAGES.get(), "fly.disabled", "&cYou have successfully disabled fly mode."),
    GIVE_SENDER(MESSAGES.get(), "give.sender", "&aYou have successfully given %amount% %item% to %player%."),
    GIVE_TARGET(MESSAGES.get(), "give.target", "&aYou have received %amount% %item% from %player%."),
    GIVE_ALL(MESSAGES.get(), "give.all", "&aYou have successfully given %amount% %item% to all players."),
    GIVE_ALL_BROADCAST(MESSAGES.get(), "give.all-broadcast", "&a%player% has given %amount% %item% to all players."),
    GIVE_YOURSELF(MESSAGES.get(), "give.yourself", "&aYou have successfully given yourself %amount% %item%."),
    ENCHANT_REMOVED(MESSAGES.get(), "enchant.removed", "&aYou have successfully removed the enchantment %enchant% from your item."),
    ENCHANT_ADDED(MESSAGES.get(), "enchant.added", "&aYou have successfully added the enchantment %enchant% to your item."),
    CLEAR_SELF(MESSAGES.get(), "clear.self", "&aYou have successfully cleared your inventory."),
    CLEAR_SENDER(MESSAGES.get(), "clear.sender", "&aYou have successfully cleared the inventory of %player%."),
    CLEAR_TARGET(MESSAGES.get(), "clear.target", "&aYour inventory has been cleared by %player%."),
    HEAL_SELF(MESSAGES.get(), "heal.self", "&aYou have successfully healed yourself."),
    HEAL_SENDER(MESSAGES.get(), "heal.sender", "&aYou have successfully healed %player%."),
    HEAL_TARGET(MESSAGES.get(), "heal.target", "&aYou have been healed by %player%."),
    FEED_SENDER(MESSAGES.get(), "feed.sender", "&aYou have successfully fed %player%."),
    FEED_TARGET(MESSAGES.get(), "feed.target", "&aYou have been fed by %player%."),
    FEED_SELF(MESSAGES.get(), "feed.self", "&aYou have successfully fed yourself."),
    DEMO_SCREEN(MESSAGES.get(), "demo-screen", "&aYou have successfully sent the demo screen to %player%."),
    KILL_ALL(MESSAGES.get(), "kill-all", "&aYou have removed all the entities. &7(%total%)"),
    MAX_PLAYERS(MESSAGES.get(), "max-players", "&aYou have successfully set the slots to %amount%."),
    ITEM_STACKED(MESSAGES.get(), "item-stacked", "&aYou have successfully stacked your item."),
    SUDO_PLAYER(MESSAGES.get(), "sudo.player", "&aYou have successfully forced %player% to say %text%"),
    SUDO_ALL_PLAYER(MESSAGES.get(), "sudo.all.player", "&aYou have successfully forced all players to say %text%"),


    /* GENERAL COMMANDS STAFF ALERTS */
    FLY_ENABLED_STAFF(MESSAGES.get(), "fly.enabled-staff", "&b[S] &3[%server%] %executor% &ehas enabled fly mode."),
    FLY_DISABLED_STAFF(MESSAGES.get(), "fly.disabled-staff", "&b[S] &3[%server%] %executor% &ehas disabled fly mode."),
    GIVE_STAFF(MESSAGES.get(), "give.sender-staff", "&b[S] &3[%server%] %executor% &ehas given %amount% %item% to %player%."),
    GIVE_ALL_STAFF(MESSAGES.get(), "give.all-staff", "&b[S] &3[%server%] %executor% &ehas given %amount% %item% to all players."),
    GIVE_SELF_STAFF(MESSAGES.get(), "give.yourself-staff", "&b[S] &3[%server%] %executor% &ehas given himself %amount% %item%."),
    CLEAR_PLAYER_STAFF(MESSAGES.get(), "clear.player-staff", "&b[S] &3[%server%] %executor% &ehas cleared the inventory of %player%."),
    HEAL_STAFF(MESSAGES.get(), "heal.staff", "&b[S] &3[%server%] %executor% &ehas healed %player%."),
    SUDO_STAFF(MESSAGES.get(), "sudo.staff", "&b[S] &3[%server%] %executor% &ehas forced %player% to say %text%"),
    SUDO_ALL_STAFF(MESSAGES.get(), "sudo.all.staff", "&b[S] &3[%server%] %executor% &ehas forced all players to say %text%"),

    /* ERROR */
    NO_PERMISSION(MESSAGES.get(), "error.no-permission", "&cYou do not have permission to execute this command."),
    RANK_ALREADY_EXISTS(MESSAGES.get(), "error.rank-already-exists", "&cA rank with that name already exists."),
    RANK_NOT_FOUND(MESSAGES.get(), "error.rank-not-found", "&cA rank with that name could not be found."),
    RANK_PERMISSION_ALREADY_EXISTS(MESSAGES.get(), "error.rank-permission-already-exists", "&cRank %rank% already has permission '%permission%'."),
    RANK_PERMISSION_DOES_NOT_EXIST(MESSAGES.get(), "error.rank-permission-does-not-exist", "&cRank %rank% does not have permission '%permission%'."),
    RANK_INHERITANCE_ALREADY_EXISTS(MESSAGES.get(), "error.rank-inheritance-already-exists", "&cRank %rank% already inherits from rank %child%."),
    RANK_INHERITANCE_DOES_NOT_EXIST(MESSAGES.get(), "error.rank-inheritance-does-not-exist", "&cRank %rank% does not inherit from rank %child%."),
    CANNOT_DELETE_DEFAULT_RANK(MESSAGES.get(), "error.cannot-delete-default-rank", "&cYou cannot delete the default rank."),
    NEED_NAME(MESSAGES.get(), "error.need-name", "&cYou need to specify a name."),
    PLAYER_NOT_FOUND(MESSAGES.get(), "error.player-not-found", "&cA player with that name could not be found."),
    PLAYER_NOT_ONLINE(MESSAGES.get(), "error.player-not-online", "&cThat player is not online."),
    CHAT_CURRENTLY_MUTED(MESSAGES.get(), "error.chat-currently-muted", "&cThe global chat is currently muted."),
    TIME_FORMAT(MESSAGES.get(), "error.time-format", "&cPlease enter a valid time format."),
    PACKET_ERROR(MESSAGES.get(), "error.packet", "&cAn error occurred while sending the packet."),
    PLAYER_ALREADY_WHITELISTED(MESSAGES.get(), "error.player-already-whitelisted", "&cThat player is already whitelisted."),
    PLAYER_NOT_WHITELISTED(MESSAGES.get(), "error.player-not-whitelisted", "&cThat player is not whitelisted."),
    SERVER_NOT_FOUND(MESSAGES.get(), "error.server-not-found", "&cA server with that name could not be found."),
    NOT_SUPPORTED(MESSAGES.get(), "error.not-supported", "&cThis feature is not supported on 1.7."),
    MAXIMUM_COORDINATE(MESSAGES.get(), "error.maximum-coordinate", "&cMaximal coordinates are +/- x: 3000000 y: 260/-10 z: 3000000"),
    CANNOT_MESSAGE_YOURSELF(MESSAGES.get(), "error.cannot-message-yourself", "&cYou cannot message yourself."),
    PLAYER_MESSAGES_DISABLED(MESSAGES.get(), "error.player-messages-disabled", "&cThat player has private messages disabled."),
    OWN_MESSAGES_DISABLED(MESSAGES.get(), "error.own-messages-disabled", "&cYou have private messages disabled."),
    NOBODY_TO_REPLY_TO(MESSAGES.get(), "error.nobody-to-reply-to", "&cYou have nobody to reply to."),
    SERVER_ALREADY_REBOOTING(MESSAGES.get(), "error.server-already-rebooting", "&cThe server is already rebooting."),
    CANNOT_REPORT_YOURSELF(MESSAGES.get(), "error.cannot-report-yourself", "&cYou cannot report yourself."),
    COOLDOWN(MESSAGES.get(), "error.cooldown", "&cYou cannot do that for another %time%."),
    INVALID_MATERIAL(MESSAGES.get(), "error.invalid-material", "&cInvalid material."),
    LEVEL_NOT_IN_BOUNDS(MESSAGES.get(), "error.level-no-in-bounds", "&cLevel must be between 0 and 10."),
    NEED_ITEM_IN_HAND(MESSAGES.get(), "error.need-item-in-hand", "&cYou need to hold an item in your hand."),
    DOES_NOT_HAVE_ENCHANTMENT(MESSAGES.get(), "error.does-not-have-enchantment", "&cThat item does not have the enchantment %enchant%."),
    CANNOT_PUNISH_PLAYER(MESSAGES.get(), "error.cannot-punish-player", "&cYou cannot punish that player."),
    PLAYER_ALREADY_PUNISHED(MESSAGES.get(), "error.player-already-punished", "&cThat player is already punished."),
    PLAYER_NOT_PUNISHED(MESSAGES.get(), "error.player-not-punished", "&cThat player is not punished."),
    ITEM_ALREADY_STACKED(MESSAGES.get(), "error.item-already-stacked", "&cThat item is already stacked."),

    /* OTHER */
    DEV_MODE(SETTINGS.get(), "dev-mode", false),
    ;


    private final String fileName;
    private final String path;
    @Setter
    private Object def;

    Locale(String fileName, String path, String def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, boolean def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, int def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, double def) {
        this.fileName = fileName;
        this.path = path;
        this.def = def;
    }

    Locale(String fileName, String path, String... def){
        this.fileName = fileName;
        this.path = path;
        this.def = Arrays.asList(def);
    }

    @SneakyThrows
    public static void init(JavaPlugin plugin) {
        for (Locale locale : values()) {
            File file = new File(plugin.getDataFolder(), locale.getFileName());
            if (!file.exists()) {
                plugin.saveResource(locale.getFileName(), false);
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            if (!yamlConfiguration.contains(locale.getPath())) {
                yamlConfiguration.set(locale.getPath(), locale.getDef());
                yamlConfiguration.save(file);
            }
            locale.setDef(yamlConfiguration.get(locale.getPath()));
        }
    }

    public String getString() {
        return CC.translate((String) this.def);
    }

    public String getRawString() {
        return (String) this.def;
    }

    public String getStringNetwork() {
        return CC.translate(CC.addNetworkPlaceholder((String) this.def));
    }

    public boolean getBoolean() {
        return (boolean) this.def;
    }

    public int getInt() {
        return (int) this.def;
    }

    public double getDouble() {
        return (double) this.def;
    }

    public List<String> getStringList() {
        return (List<String>) this.def;
    }




    enum FileName {
        SETTINGS("settings.yml"),
        MESSAGES("messages.yml");

        private final String fileName;

        FileName(String fileName) {
            this.fileName = fileName;
        }

        public String get() {
            return fileName;
        }
    }
}
