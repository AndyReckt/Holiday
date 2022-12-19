package me.andyreckt.holiday.api.user;

public interface IStaffAlerts {

    boolean isReportAlerts();
    boolean isRequestAlerts();
    boolean isChatAlerts();
    boolean isGamemodeAlerts();
    boolean isTeleportAlerts();
    boolean isWhitelistAlerts();
    boolean isServerAlerts();
    boolean isServerManagerAlerts();
    boolean isBannedLoginAlerts();
    boolean isAltLoginAlerts();

    void setReportAlerts(boolean reportAlerts);
    void setRequestAlerts(boolean requestAlerts);
    void setChatAlerts(boolean chatAlerts);
    void setGamemodeAlerts(boolean gamemodeAlerts);
    void setTeleportAlerts(boolean teleportAlerts);
    void setWhitelistAlerts(boolean whitelistAlerts);
    void setServerAlerts(boolean serverAlerts);
    void setServerManagerAlerts(boolean serverManagerAlerts);
    void setBannedLoginAlerts(boolean bannedLoginAlerts);
    void setAltLoginAlerts(boolean altLoginAlerts);
}
