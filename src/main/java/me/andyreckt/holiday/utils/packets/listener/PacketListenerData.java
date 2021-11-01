package me.andyreckt.holiday.utils.packets.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.andyreckt.holiday.utils.packets.Packet;

import java.lang.reflect.Method;

/**
 * A wrapper class that holds all the information needed to
 * identify and execute a message function.
 */
@AllArgsConstructor
@Getter
public class PacketListenerData {

    private final Object instance;
    private final Method method;
    private final Class<? extends Packet> packetClass;

    public boolean matches(Packet packet) {
        return this.packetClass == packet.getClass();
    }

}
