package me.andyreckt.holiday.database.redis.packet;

import me.andyreckt.holiday.utils.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;

@AllArgsConstructor @Getter
public class ClickablePacket implements Packet {

    String message;
    String hoverMessage;
    ClickEvent.Action clickAction;
    String clickCmd;

}
