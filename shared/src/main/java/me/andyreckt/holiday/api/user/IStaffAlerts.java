package me.andyreckt.holiday.api.user;

public interface IStaffAlerts {

    boolean isReportAlerts();
    boolean isRequestAlerts();
    boolean isChatAlerts();
    boolean isAbuseAlerts();
    boolean isWhitelistAlerts();
    boolean isServerAlerts();
    boolean isServerManagerAlerts();
    boolean isBannedLoginAlerts();
    boolean isAltLoginAlerts();
    boolean isSilentPunishmentAlerts();

    void setReportAlerts(boolean reportAlerts);
    void setRequestAlerts(boolean requestAlerts);
    void setChatAlerts(boolean chatAlerts);
    void setAbuseAlerts(boolean gamemodeAlerts);
    void setWhitelistAlerts(boolean whitelistAlerts);
    void setServerAlerts(boolean serverAlerts);
    void setServerManagerAlerts(boolean serverManagerAlerts);
    void setBannedLoginAlerts(boolean bannedLoginAlerts);
    void setAltLoginAlerts(boolean altLoginAlerts);
    void setSilentPunishmentAlerts(boolean silentPunishmentAlerts);
}
