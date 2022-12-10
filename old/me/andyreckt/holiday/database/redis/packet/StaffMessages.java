package me.andyreckt.holiday.database.redis.packet;

import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.utils.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class StaffMessages {

    @Getter
    public static class StaffMessagesPacket implements Packet {

        final String message;
        final StaffMessageType channel;
        String hoverMsg;
        String clickCmd;

        public StaffMessagesPacket(String message, StaffMessageType channel) {
            this.message = message;
            this.channel = channel;
        }

        public StaffMessagesPacket(String message, StaffMessageType channel, String hovermsg, String clickCmd) {
            this.message = message;
            this.channel = channel;
            this.hoverMsg = hovermsg;
            this.clickCmd = clickCmd;
        }


    }

    @Getter @AllArgsConstructor
    public static class ReportPacket implements Packet {

        String reporter;
        String reported;
        String reason;
        String server;

    }
    @Getter @AllArgsConstructor
    public static class HelpopPacket implements Packet {

        String sender;
        String request;
        String server;

    }


}
