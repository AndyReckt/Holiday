package me.andyreckt.holiday.core.util.duration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Duration {
    public static final Duration PERMANENT = Duration.of(TimeUtil.PERMANENT);

    private final long duration;

    public long get() {
        return duration;
    }

    public boolean isPermanent() {
        return duration == TimeUtil.PERMANENT;
    }

    public String getFormatted() {
        return TimeUtil.getDuration(duration);
    }

    public String toNiceTime() {
        return TimeUtil.niceTime(duration, true, true);
    }

    public String toRoundedTime() {
        return TimeUtil.millisToRoundedTime(duration);
    }

    public String toSmallRoundedTime() {
        return TimeUtil.millisToSmallRoundedTime(duration);
    }

    public static Duration of(long duration) {
        return new Duration(duration);
    }

    public static Duration of(String duration) {
        return new Duration(TimeUtil.getDuration(duration));
    }

}
