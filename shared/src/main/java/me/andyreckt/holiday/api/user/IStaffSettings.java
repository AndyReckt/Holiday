package me.andyreckt.holiday.api.user;

public interface IStaffSettings {

    boolean isSocialSpy();
    boolean isVanished();

    IStaffAlerts getAlerts();

    boolean isStaffChat();
    boolean isAdminChat();

    void setSocialSpy(boolean socialSpy);
    void setVanished(boolean vanished);

    void setStaffChat(boolean staffChat);
    void setAdminChat(boolean adminChat);

    //TODO: Change the alerts to their own object. -> Alerts for every staff command to limit abuse.

}
