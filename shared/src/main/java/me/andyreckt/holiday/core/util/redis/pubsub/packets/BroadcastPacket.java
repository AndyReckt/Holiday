package me.andyreckt.holiday.core.util.redis.pubsub.packets;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.enums.AlertType;
import me.andyreckt.holiday.core.util.redis.messaging.Packet;

@Getter
@RequiredArgsConstructor
public class BroadcastPacket implements Packet {

    private final String message;
    private final String permission;
    private final AlertType alertType;

    public BroadcastPacket(String message) {
        this.message = message;
        this.permission = null;
        this.alertType = null;
    }

    public BroadcastPacket(String message, String permission) {
        this.message = message;
        this.permission = permission;
        this.alertType = null;
    }

    @Override
    public void onReceive() {
        if (HolidayAPI.getUnsafeAPI().getBroadcastConsumer() != null) {
            HolidayAPI.getUnsafeAPI().getBroadcastConsumer().accept(this);
        }
    }
}
