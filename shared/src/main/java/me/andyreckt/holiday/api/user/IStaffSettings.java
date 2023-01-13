package me.andyreckt.holiday.api.user;

public interface IStaffSettings {

    boolean isSocialSpy();
    boolean isVanished();
    boolean isStaffMode();

    IStaffAlerts getAlerts();

    boolean isStaffChat();
    boolean isAdminChat();

    void setSocialSpy(boolean socialSpy);
    void setVanished(boolean vanished);
    void setStaffMode(boolean staffMode);

    void setStaffChat(boolean staffChat);
    void setAdminChat(boolean adminChat);

}
