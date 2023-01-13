package me.andyreckt.holiday.staff.util.sunset.parameter;

import lombok.Getter;
import me.andyreckt.holiday.staff.util.sunset.annotations.Param;

@Getter
public class PData {

    private final String name;
    private final boolean wildcard;
    private final String baseValue;
    private final boolean required;
    private final String[] tabComplete;
    private final Class<?> clazz;


    public PData(Param parameter, Class<?> clazz) {
        this.name = parameter.name();
        this.wildcard = parameter.wildcard();
        this.baseValue = parameter.baseValue();
        this.required = baseValue.equalsIgnoreCase("");
        this.tabComplete = parameter.tabCompleteFlags();
        this.clazz = clazz;
    }
}
