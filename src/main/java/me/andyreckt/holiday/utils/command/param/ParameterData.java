package me.andyreckt.holiday.utils.command.param;

import lombok.Getter;

public final class ParameterData {

    @Getter
    final String name;
    @Getter
    final boolean wildcard;
    @Getter
    final String defaultValue;
    @Getter
    final String[] tabCompleteFlags;
    @Getter
    final Class<?> parameterClass;

    public ParameterData(Param paramAnnotation, Class<?> parameterClass) {
        this.name = paramAnnotation.name();
        this.wildcard = paramAnnotation.wildcard();
        this.defaultValue = paramAnnotation.defaultValue();
        this.tabCompleteFlags = paramAnnotation.tabCompleteFlags();
        this.parameterClass = parameterClass;
    }

}