package me.andyreckt.holiday.core.util.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import me.andyreckt.holiday.core.util.json.adapter.ExlusionStrategyAdapter;
import me.andyreckt.holiday.core.util.json.adapter.LongTypeAdapter;
import me.andyreckt.holiday.core.util.json.adapter.PostProcessAdapterFactory;
import me.andyreckt.holiday.core.util.json.adapter.UUIDTypeAdapter;

import java.util.UUID;
import java.util.function.Function;

public class GsonProvider {

    private static GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapterFactory(new PostProcessAdapterFactory())
            .setExclusionStrategies(new ExlusionStrategyAdapter())
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(Long.class, new LongTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .serializeNulls()
            .setLenient();

    public static Gson GSON = gsonBuilder.create();

    public static void useGsonBuilderThenRebuild(Function<GsonBuilder, GsonBuilder> function) {
        synchronized (gsonBuilder) {
            gsonBuilder = function.apply(gsonBuilder);
            GSON = gsonBuilder.create();
        }
    }
}
