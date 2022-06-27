package me.andyreckt.holiday.commands.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.mongo.MongoUtils;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.server.menu.ServersMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.PlayerUtil;
import me.andyreckt.holiday.utils.PunishmentUtils;
import me.andyreckt.holiday.utils.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class HolidayCommand {

    @Command(names = {"holiday", "holiday info", "core", "core info"})
    public static void holiday(Player sender) {

        if (!(sender.hasPermission("holiday.op") || sender.hasPermission("holiday.*") || sender.hasPermission("*") || sender.hasPermission("*.*"))) {
            sender.sendMessage(CC.translate("&eThis server is running &bHoliday &7(&dv" + Holiday.getInstance().getDescription().getVersion() + "&7) &eMade by &dAndyReckt"));
            return;
        }

        sender.sendMessage(CC.CHAT_BAR);
        sendVersionMessage(sender);
        sender.sendMessage(CC.translate("&dCommands:"));
        sender.sendMessage(CC.translate(" &e/holiday punishments &7(Shows the amount of Punishments)"));
        sender.sendMessage(CC.translate(" &e/holiday clearpunishments &7(Clears the punishments collection)"));
        sender.sendMessage(CC.translate(" &e/holiday clearprofiles &7(Clears the profiles collection)"));
        sender.sendMessage(CC.translate(" &e/holiday cleardisguises &7(Clears the disguises collection)"));
        sender.sendMessage(CC.translate(" &e/holiday clearservers &7(Clears the servers collection)"));
        sender.sendMessage(CC.translate(" &e/holiday reloadconfig &7(Reload the config files)"));
        sender.sendMessage(CC.translate(" &e/holiday servers &7(Show a list of all servers)"));
        sender.sendMessage(CC.CHAT_BAR);

    }

    @Command(names = {"holiday punishments", "core punishments"}, perm = "holiday.op")
    public static void punishments(CommandSender sender) {
        int bans = PunishmentUtils.bans().size();
        int mutes = PunishmentUtils.mutes().size();
        int ipbans = PunishmentUtils.ipbans().size();
        int blacklists = PunishmentUtils.blacklists().size();

        sender.sendMessage(CC.CHAT_BAR);
        sendVersionMessage(sender);
        sender.sendMessage(CC.translate("&dPunishments:"));
        sender.sendMessage(CC.translate(" &eBans: &7" + bans));
        sender.sendMessage(CC.translate(" &eIp Bans: &7" + ipbans));
        sender.sendMessage(CC.translate(" &eBlacklists: &7" + blacklists));
        sender.sendMessage(CC.translate(" &eMutes: &7" + mutes));
        sender.sendMessage(CC.CHAT_BAR);
    }

    @Command(names = {"holiday clearpunishments", "core clearpunishments", "holiday wipepunishments", "core wipepunishments"}, perm = "holiday.op")
    public static void clearpunishments(Player sender) {
        ConversationFactory factory = new ConversationFactory(Holiday.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear the punishment collection? This action CANNOT be reversed (only do this if this server is the only one left online). Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    MongoUtils.getPunishmentsCollection().drop();
                    Holiday.getInstance().getPunishmentHandler().clearCache();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Punishment collection has been cleared.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Database clear aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Command(names = {"holiday cleardisguises", "core cleardisguises", "holiday wipedisguises", "core wipedisguises"}, perm = "holiday.op")
    public static void cleardisguise(Player sender) {
        ConversationFactory factory = new ConversationFactory(Holiday.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear the disguise collection? This action CANNOT be reversed (only do this if this server is the only one left online). Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    MongoUtils.getDisguiseCollection().drop();
                    Holiday.getInstance().getDisguiseHandler().usedNames.clear();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Disguise collection has been cleared.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Database clear aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Command(names = {"holiday clearprofiles", "core clearprofiles", "holiday wipeprofiles", "core wipeprofiles"}, perm = "holiday.op")
    public static void clearprofiles(Player sender) {
        ConversationFactory factory = new ConversationFactory(Holiday.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear the profile collection? This action CANNOT be reversed (only do this if this server is the only one left online). Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    MongoUtils.getProfileCollection().drop();
                    Holiday.getInstance().getProfileHandler().cachedProfiles().clear();
                    Bukkit.getServer().shutdown();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Profile collection has been cleared.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Database clear aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Command(names = {"holiday clearservers", "core clearservers", "holiday wipeservers", "core wipeservers"}, perm = "holiday.op")
    public static void clearservers(Player sender) {
        ConversationFactory factory = new ConversationFactory(Holiday.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear the server collection? This action CANNOT be reversed (only do this if this server is the only one left online). Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    MongoUtils.getServersCollection().drop();
                    Holiday.getInstance().getServerHandler().getServers().clear();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Server collection has been cleared.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Database clear aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Command(names = {"holiday reload", "holiday reloadconfig"}, perm = "holiday.op")
    public static void reloadfiles(Player sender) {
        Holiday.getInstance().setupConfigFiles();
        sender.sendMessage(CC.translate("&aConfig files sucessfully reloaded."));
    }
    @Command(names = {"holiday servers", "servers"}, perm = "holiday.op")
    public static void servers(Player sender) {
        new ServersMenu().openMenu(sender);
    }

    private static void sendVersionMessage(CommandSender sender) {
        sender.sendMessage(CC.translate("&bHoliday &7(&dv" + Holiday.getInstance().getDescription().getVersion() + "&7) &eMade by &dAndyReckt"));
    }


}
