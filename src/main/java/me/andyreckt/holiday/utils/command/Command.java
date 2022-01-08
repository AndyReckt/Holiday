package me.andyreckt.holiday.utils.command;

import me.andyreckt.holiday.player.rank.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String[] names();

    String perm() default "";

    boolean async() default false;

}