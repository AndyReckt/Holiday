package me.andyreckt.holiday.bukkit.util.sunset.annotations;

import me.andyreckt.holiday.bukkit.util.files.Perms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MainCommand {
    String[] names();

    Perms permission() default Perms.NONE;

    String description() default "";

    String usage() default "none";

    String helpCommand() default "help";

    boolean autoHelp() default true;
}
