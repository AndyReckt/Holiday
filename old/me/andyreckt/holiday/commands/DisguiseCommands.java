package me.andyreckt.holiday.commands;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.disguise.impl.v1_7.DisguiseHandler_1_7;
import me.andyreckt.holiday.player.disguise.impl.v1_8.DisguiseHandler_1_8;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.server.nms.impl.NMS_v1_7;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Cooldown;
import me.andyreckt.holiday.utils.NumberUtils;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.file.type.BasicConfigurationFile;
import me.andyreckt.sunset.annotations.Command;
import me.andyreckt.sunset.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class DisguiseCommands {

    private static final Map<UUID, Cooldown> cooldownMap = new HashMap<>();
    private static final List<String> names = new ArrayList<>(), skins = new ArrayList<>();

    @Command(names = {"mnick", "mdisguise", "mdis", "manualdisguise", "manualnick"}, permission = "holiday.manualdisguise")
    public void manual(Player player,
                              @Param(name = "name") String name,
                              @Param(name = "skin") String skin,
                              @Param(name = "rank", baseValue = "default") Rank rank) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(player);
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (profile.isDisguised()) {
            player.sendMessage(CC.translate(messages.getString("COMMANDS.DISGUISE.ALREADY")));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Holiday.getInstance(), () -> {
            if (name.length() > 16) {
                player.sendMessage(CC.translate(messages.getString("COMMANDS.DISGUISE.INVALIDLENGHT")));
                return;
            }

            if (skin.length() > 16) {
                player.sendMessage(CC.translate(messages.getString("COMMANDS.DISGUISE.INVALIDLENGHT")));
                return;
            }

            try {
                Holiday.getInstance().getDisguiseHandler().disguise(player, rank, skin, name, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Command(names = {"nick", "disguise", "dis"}, permission = "holiday.disguise")
    public void nick(Player player) {

        Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(player);
        BasicConfigurationFile messages = Holiday.getInstance().getMessages();

        if (profile.isDisguised()) {
            player.sendMessage(CC.translate(messages.getString("COMMANDS.DISGUISE.ALREADY")));
            return;
        }

        if (cooldownMap.containsKey(player.getUniqueId())) {
            Cooldown oldCd = cooldownMap.get(player.getUniqueId());
            if (oldCd.hasExpired()) cooldownMap.remove(player.getUniqueId());
            else {
                player.sendMessage(CC.translate(messages.getString("COMMANDS.DISGUISE.COOLDOWN").replace("<cooldown>", TimeUtil.getDuration(oldCd.getRemaining()))));
                return;
            }
        }
        String name, skin;

        name = names.get(NumberUtils.generateRandomIntInRange(0, names.size() - 1));
        if (Holiday.getInstance().getDisguiseHandler() instanceof DisguiseHandler_1_7) {
            while (DisguiseHandler_1_7.DisguiseRequest.alreadyUsed(name))
                name = names.get(NumberUtils.generateRandomIntInRange(0, names.size() - 1));
        } else {
            while (DisguiseHandler_1_8.DisguiseRequest.alreadyUsed(name))
                name = names.get(NumberUtils.generateRandomIntInRange(0, names.size() - 1));
        }

        final String namef = name;

        skin = skins.get(NumberUtils.generateRandomIntInRange(0, skins.size() - 1));

        Bukkit.getScheduler().runTaskAsynchronously(Holiday.getInstance(), () -> {
            try {
                Holiday.getInstance().getDisguiseHandler().disguise(player, Holiday.getInstance().getRankHandler().getDefaultRank(), skin, namef, true);
                Cooldown cd = Cooldown.fromMinutes(15);
                if (Holiday.getInstance().getProfileHandler().getByUUID(player.getUniqueId()).isStaff())
                    cd = Cooldown.fromSeconds(30);
                cooldownMap.put(player.getUniqueId(), cd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Command(names = {"undisguise", "unnick", "undis"})
    public void undis(Player player) {

        Profile mineman = Holiday.getInstance().getProfileHandler().getByUUID(player.getUniqueId());

        if (!mineman.isDisguised()) {
            player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("COMMANDS.DISGUISE.NOTDISGUISED")));
        } else {
            Holiday.getInstance().getDisguiseHandler().undisguise(player, true);
        }

    }

    @Command(names = {"disguiselist", "nicklist"}, permission = "holiday.disguiselist")
    public void list(CommandSender sender) {

        StringBuilder sb = new StringBuilder();
        if (Holiday.getInstance().getNmsHandler() instanceof NMS_v1_7) {
            DisguiseHandler_1_7 handler = (DisguiseHandler_1_7) Holiday.getInstance().getDisguiseHandler();

            if (!handler.usedNames.isEmpty()) {
                handler.usedNames.forEach(n -> {
                    Profile profile = Holiday.getInstance().getProfileHandler().getByName(n);
                    if (profile != null) {
                        String server = (profile.getCurrentServer() == null) ? "&coffline" : profile.getCurrentServer();

                        sb.append(Holiday.getInstance().getMessages().getString("COMMANDS.DISGUISE.LIST")
                                .replace("<displayname>", profile.getDisplayNameWithColor())
                                .replace("<name>", profile.getNameWithColor())
                                .replace("<server>", server));
                        sb.append("\n");
                    }
                });
            } else {
                sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.DISGUISE.NOBODYDISGUISED"));
                return;
            }
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&aDisguised Players:"));
            sender.sendMessage(CC.translate(sb.substring(0, sb.length() - 1)));
            sender.sendMessage(CC.CHAT_BAR);
        } else {
            DisguiseHandler_1_8 handler = (DisguiseHandler_1_8) Holiday.getInstance().getDisguiseHandler();

            if (!handler.usedNames.isEmpty()) {
                handler.usedNames.forEach(n -> {
                    Profile profile = Holiday.getInstance().getProfileHandler().getByName(n);
                    if (profile != null) {
                        String server = (profile.getCurrentServer() == null) ? "&coffline" : profile.getCurrentServer();

                        sb.append(Holiday.getInstance().getMessages().getString("COMMANDS.DISGUISE.LIST")
                                .replace("<displayname>", profile.getDisplayNameWithColor())
                                .replace("<name>", profile.getNameWithColor())
                                .replace("<server>", server));
                        sb.append("\n");
                    }
                });
            } else {
                sender.sendMessage(Holiday.getInstance().getMessages().getString("COMMANDS.DISGUISE.NOBODYDISGUISED"));
                return;
            }
            sender.sendMessage(CC.CHAT_BAR);
            sender.sendMessage(CC.translate("&aDisguised Players:"));
            sender.sendMessage(CC.translate(sb.substring(0, sb.length() - 1)));
            sender.sendMessage(CC.CHAT_BAR);
        }
    }

    public static void setup(BasicConfigurationFile settings) {
        skins.addAll(settings.getStringList("DISGUISE.SKINS"));
        names.addAll(settings.getStringList("DISGUISE.NAMES"));
    }
}
