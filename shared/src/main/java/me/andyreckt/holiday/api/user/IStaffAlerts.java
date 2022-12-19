package me.andyreckt.holiday.api.user;

public interface IStaffAlerts {

    boolean isReportAlerts();
    boolean isRequestAlerts();

    void setReportAlerts(boolean reportAlerts);
    void setRequestAlerts(boolean requestAlerts);
}
