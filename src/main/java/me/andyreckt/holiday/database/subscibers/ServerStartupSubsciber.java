package me.andyreckt.holiday.database.subscibers;

import com.google.gson.JsonObject;
import io.github.zowpy.jedisapi.redis.subscription.IncomingMessage;
import io.github.zowpy.jedisapi.redis.subscription.JedisSubscriber;

public class ServerStartupSubsciber extends JedisSubscriber {

    @IncomingMessage(payload = "server")
    public void server(JsonObject object) {
        String type = object.get("type").toString();
        if(type.equalsIgnoreCase("serverstart")) {

        }
    }

}
