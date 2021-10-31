package me.andyreckt.holiday.database.utils;

import me.andyreckt.holiday.Files;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.Redis;
import me.andyreckt.holiday.punishments.PunishData;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.json.JsonBuilder;

import java.util.List;
import java.util.concurrent.Executor;

public class RedisUtils {


    public static void sendToAll(List<String> msg) {
        Redis.sendPayload("server",
                new JsonBuilder()
                        .append("type", "broadcast_list")
                        .append("text", msg.toString())
                        .get());
    }

    public static void sendToAll(String message) {
        Redis.sendPayload("server",
                new JsonBuilder()
                        .append("type", "broadcast")
                        .append("text", message)
                        .get());
    }

    public static void sendServerStartup() {
        Redis.sendPayload("server",
                new JsonBuilder()
                        .append("type", "serverStart")
                        .append("serverName", Files.Config.SERVER_NAME.getString())
                        .get());
    }

    public static void sendMessageToServer(String message, String server) {
        Redis.sendPayload("server",
                new JsonBuilder()
                        .append("type", "broadcast_server")
                        .append("text", CC.translate(message))
                        .append("server", server)
                        .get());
    }

    public static void sendPunishmentAdd(PunishData punishData) {
        Redis.sendPayload("punishment",
                new JsonBuilder()
                        .append("type", "add")
                        .append("punished", punishData.getPunished().getUuid().toString())
                        .append("punishType", punishData.getType().toString())
                        .append("issuer", punishData.getAddedBy().getUuid().toString())
                        .append("duration", punishData.getDuration())
                        .append("reason", punishData.getAddedReason())
                        .append("silent", punishData.isSilent())
                        .append("serverName", Files.Config.SERVER_NAME.getString())
                        .get());
    }


    public static Executor getExecutor() {
        return Holiday.getInstance().getDbExecutor();
    }

}
