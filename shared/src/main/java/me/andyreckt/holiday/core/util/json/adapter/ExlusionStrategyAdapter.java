package me.andyreckt.holiday.core.util.json.adapter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import me.andyreckt.holiday.core.util.json.annotation.Exclude;

public class ExlusionStrategyAdapter implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        return field.getAnnotation(Exclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(Exclude.class) != null;
    }
}
