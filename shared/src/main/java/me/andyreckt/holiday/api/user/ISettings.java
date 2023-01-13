package me.andyreckt.holiday.api.user;

public interface ISettings {

    boolean isPrivateMessages();
    boolean isPrivateMessagesSounds();

    void setPrivateMessages(boolean privateMessages);
    void setPrivateMessagesSounds(boolean privateMessagesSounds);

}
