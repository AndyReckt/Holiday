package me.andyreckt.holiday.bukkit.util.other;

import lombok.Data;

import java.text.DecimalFormat;

@Data
public class Cooldown {

    private long start = System.currentTimeMillis();
    private long expire;

    public Cooldown(long durationMillis) {
        this.expire = this.start + durationMillis;
    }

    public static Cooldown fromSeconds(int seconds) {
        return new Cooldown(seconds * 1000L);
    }

    public static Cooldown fromMinutes(int minutes) {
        return new Cooldown(minutes * 60 * 1000L);
    }

    public static Cooldown fromHours(int hours) {
        return new Cooldown(hours * 24 * 60 * 1000L);
    }

    public long getPassed() {
        return System.currentTimeMillis() - this.start;
    }

    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - this.expire >= 0;
    }

    public String getTimeLeft() {
        return formatSeconds(this.getRemaining());
    }

    public String formatSeconds(long time) {
        return new DecimalFormat("#0.0").format(time / 1000.0F);
    }
}
