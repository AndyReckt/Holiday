package me.andyreckt.holiday.bukkit.util.sunset.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Flag {

    char identifier();

    String name() default "flag";

    boolean baseValue() default false;

}
