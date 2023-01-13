package me.andyreckt.holiday.staff.util.sunset.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    String name();

    boolean wildcard() default false;

    String baseValue() default "";

    String[] tabCompleteFlags() default "";

}