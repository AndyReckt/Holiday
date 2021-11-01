package me.andyreckt.holiday.database.subscibers;

import com.google.gson.JsonObject;
import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.punishments.PunishmentType;
import me.andyreckt.holiday.rank.Rank;
import me.andyreckt.holiday.utils.TimeUtil;
import me.andyreckt.holiday.utils.packets.listener.PacketListener;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PunishmentSubscriber implements PacketListener {

    public void add(JsonObject object) {
        String type = object.get("type").toString();
        if(type.equalsIgnoreCase("add")) {
            PunishmentType punishmentType = PunishmentType.getByName(object.get("punishType").getAsString());
            String string = "";
            switch (punishmentType) {
                case BAN: string = Files.Messages.BAN_MESSAGE.getString();
                case KICK: string = Files.Messages.KICK_MESSAGE.getString();
                case MUTE: string = Files.Messages.MUTE_MESSAGE.getString();
                case WARN: string = Files.Messages.WARN_MESSAGE.getString();
                case IP_BAN: string = Files.Messages.IP_BAN_MESSAGE.getString();
                case TEMP_BAN: string = Files.Messages.TEMP_BAN_MESSAGE.getString();
                case TEMP_MUTE: string = Files.Messages.TEMP_MUTE_MESSAGE.getString();
                case BLACKLIST: string = Files.Messages.TEMP_BAN_MESSAGE.getString();
            }

            Profile issuer = Profile.getFromUUID(UUID.fromString(object.get("issuer").getAsString()));
            Profile punished = Profile.getFromUUID(UUID.fromString(object.get("punished").getAsString()));

            string = string.replace("<executor>", issuer.getNameWithColor());
            string = string.replace("<player>", punished.getNameWithColor());
            string = string.replace("<reason>", object.get("reason").getAsString());
            string = string.replace("<duration>", TimeUtil.getDuration(object.get("duration").getAsLong()));


            if(object.get("silent").getAsBoolean()) {
                String fString = Files.Messages.SILENT_PREFIX.getString() + string;
                Profile.getAllProfiles().forEach(profile -> {
                    Rank rank = profile.getHighestRank();
                    if(rank.isStaff() || rank.isAdmin() || rank.isDev()) {
                        profile.getPlayer().sendMessage(fString);
                    }
                });
            } else {
                Bukkit.broadcastMessage(string);
            }


        }
    }
}
