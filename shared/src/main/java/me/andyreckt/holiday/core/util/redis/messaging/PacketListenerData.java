package me.andyreckt.holiday.core.util.redis.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * A wrapper class that holds all the information needed to
 * identify and execute a message function.
 */
@AllArgsConstructor
@Getter
public class PacketListenerData {

    final Object instance;
    final Method method;
    final Class<? extends Packet> packetClass;

    public boolean matches(Packet packet) {
        return this.packetClass == packet.getClass();
    }

}