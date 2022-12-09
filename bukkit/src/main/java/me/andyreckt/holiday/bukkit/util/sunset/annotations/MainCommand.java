package me.andyreckt.holiday.bukkit.util.sunset.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MainCommand {
    String[] names();

    String permission() default "";

    String description() default "";

    String usage() default "none";

    String helpCommand() default "help";

    boolean autoHelp() default true;
}
