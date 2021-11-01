package me.andyreckt.holiday.utils.packets.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a method and packet type assigned to the method.
 * The only valid parameter types for a method that is annotated
 * by this annotation are String and Packet.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IncomingPacketHandler {

}
