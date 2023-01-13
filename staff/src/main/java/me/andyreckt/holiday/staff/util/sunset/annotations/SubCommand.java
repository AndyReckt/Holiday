package me.andyreckt.holiday.staff.util.sunset.annotations;

import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.staff.util.files.SPerms;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

    String[] names();

    SPerms permission() default SPerms.NONE;

    boolean async() default false;

    String description() default "";

    String usage() default "none";
}
