package me.andyreckt.holiday.core.util.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.andyreckt.holiday.core.util.json.adapter.LongTypeAdapter;
import me.andyreckt.holiday.core.util.json.adapter.UUIDTypeAdapter;

import java.util.UUID;

public class GsonProvider {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(Long.class, new LongTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .serializeNulls()
            .setLenient()
            .create();
}
