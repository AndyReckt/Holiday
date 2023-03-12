package me.andyreckt.holiday.core.util.json.adapter;

import com.google.gson.*;
import me.andyreckt.holiday.core.util.duration.Duration;

import java.lang.reflect.Type;

public class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(String.valueOf(src.get()));
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Duration.of(Long.parseLong(json.getAsString()));
    }
}
