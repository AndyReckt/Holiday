package me.andyreckt.holiday.bukkit.util.sunset.annotations;

import me.andyreckt.holiday.bukkit.util.files.Perms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String[] names();

    Perms permission() default Perms.NONE;

    boolean async() default false;

    String description() default "";

    String usage() default "none";

}