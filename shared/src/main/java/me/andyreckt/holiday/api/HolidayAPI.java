package me.andyreckt.holiday.api;

public class HolidayAPI {
    public static me.andyreckt.holiday.core.HolidayAPI getInstance() {
        return me.andyreckt.holiday.core.HolidayAPI.getUnsafeAPI();
    }
}
