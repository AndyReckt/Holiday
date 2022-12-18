package me.andyreckt.holiday.api.user;

public interface IStaffSettings {

    boolean isSocialSpy();
    boolean isVanished();
    boolean isStaffChat();
    boolean isAdminChat();
    boolean isReportAlerts();
    boolean isRequestAlerts();

    void setSocialSpy(boolean socialSpy);
    void setVanished(boolean vanished);
    void setStaffChat(boolean staffChat);
    void setAdminChat(boolean adminChat);
    void setReportAlerts(boolean reportAlerts);
    void setRequestAlerts(boolean requestAlerts);

}
