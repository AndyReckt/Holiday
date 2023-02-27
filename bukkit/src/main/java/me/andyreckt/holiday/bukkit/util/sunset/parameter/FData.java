package me.andyreckt.holiday.bukkit.util.sunset.parameter;

import lombok.Getter;
import me.andyreckt.holiday.bukkit.util.sunset.annotations.Flag;

@Getter
public class FData implements IData {

    private final String name;
    private final char identifier;
    private final boolean baseValue;
    private final Class<?> clazz;

    public FData(Flag ann, Class<?> paramClass) {
        this.name = ann.name();
        this.identifier = ann.identifier();
        this.baseValue = ann.baseValue();
        this.clazz = paramClass;
    }

    public String getFlag() {
        return "-" + identifier;
    }
}
