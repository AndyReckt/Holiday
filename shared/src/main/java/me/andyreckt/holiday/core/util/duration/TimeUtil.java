package me.andyreckt.holiday.core.util.duration;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

     private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //TODO: make this configurable
     public static final long PERMANENT = -1;
     private static final ThreadLocal<DecimalFormat> SECONDS = ThreadLocal.withInitial(() -> new DecimalFormat("0.#"));
     private static final ThreadLocal<DecimalFormat> TRAILING = ThreadLocal.withInitial(() -> new DecimalFormat("0"));

    private static String formatDuration(long input) {
        return DurationFormatUtils.formatDurationWords(input, true, true);
    }

    public static String formatDate(long value) {
        return FORMAT.format(new Date(value));
    }

    //TODO: replace most occurences to Duration#of(long).getFomatted()
    public static String getDuration(long input) {
        return input == PERMANENT ? "Permanent" : formatDuration(input);
    }

    public static long getDuration(String input) { //TODO: replace most occurences to Duration#of(String)
        input = input.toLowerCase();

        if (Character.isLetter(input.charAt(0))) {
            return PERMANENT;
        }

        long result = 0L;

        StringBuilder number = new StringBuilder();

        for(int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if(Character.isDigit(c)) {
                number.append(c);
            } else {
                String str = number.toString();

                if(Character.isLetter(c) && !str.isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }

        return result;
    }

     static long convert(int value, char charType) {
        switch(charType) {
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'w':
                return value * TimeUnit.DAYS.toMillis(7L);
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
            default:
                return -1L;
        }
    }

    public static String niceTime(int i) {
        int r = i * 1000;
        int sec = r / 1000 % 60;
        int min = r / 60000 % 60;
        int h = r / 3600000 % 24;
        return (h > 0 ? (h < 10 ? "0" : "") + h + ":" : "") + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
    }

    public static String niceTime(long millis, boolean milliseconds) {
        return niceTime(millis, milliseconds, true);
    }

    public static String niceTime(long duration, boolean milliseconds, boolean trail) {
        return milliseconds && duration < TimeUnit.MINUTES.toMillis(1) ? (trail ? TRAILING : SECONDS).get().format((double) duration * 0.001) + 's' : DurationFormatUtils.formatDuration(duration, (duration >= TimeUnit.HOURS.toMillis(1) ? "HH:" : "") + "mm:ss");
    }

    public static String formatSimplifiedDateDiff(long date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(date);

        return formatSimplifiedDateDiff(new GregorianCalendar(), calendar);
    }

    public static String formatSimplifiedDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;

        if(toDate.equals(fromDate)) {
            return "now";
        }

        if(toDate.after(fromDate)) {
            future = true;
        }

        StringBuilder sb = new StringBuilder();
        int[] types = new int[]{1, 2, 5, 11, 12, 13};
        String[] names = new String[]{"y", "y", "m", "m", "d", "d", "h", "h", "m", "m", "s", "s"};
        int accuracy = 0;

        for(int i = 0; i < types.length && accuracy <= 2; ++i) {
            int diff = dateDiff(types[i], fromDate, toDate, future);

            if(diff <= 0) {
                continue;
            }

            ++accuracy;
            sb.append(diff).append(names[i * 2 + (diff > 1 ? 1 : 0)]);
        }

        return sb.length() == 0 ? "now" : sb.toString().trim();
    }

    static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();

        while(future && !fromDate.after(toDate) || !future && !fromDate.before(toDate)) {
            savedDate = fromDate.getTimeInMillis();

            fromDate.add(type, future ? 1 : -1);

            ++diff;
        }

        fromDate.setTimeInMillis(savedDate);
        return --diff;
    }

    public static String millisToRoundedTime(long millis) {
        if (millis == PERMANENT) {
            return "Permanent";
        }
        ++millis;
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;
        if (years > 0L) {
            return years + " year" + (years == 1L ? "" : "s");
        } else if (months > 0L) {
            return months + " month" + (months == 1L ? "" : "s");
        } else if (weeks > 0L) {
            return weeks + " week" + (weeks == 1L ? "" : "s");
        } else if (days > 0L) {
            return days + " day" + (days == 1L ? "" : "s");
        } else if (hours > 0L) {
            return hours + " hour" + (hours == 1L ? "" : "s");
        } else {
            return minutes > 0L ? minutes + " minute" + (minutes == 1L ? "" : "s") : seconds + " second" + (seconds == 1L ? "" : "s");
        }
    }

    public static String millisToSmallRoundedTime(long millis) {
        if (millis == PERMANENT) {
            return "Permanent";
        }
        ++millis;
        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;
        if (years > 0L) {
            return years + "y";
        } else if (months > 0L) {
            return months + "M";
        } else if (weeks > 0L) {
            return weeks + "w";
        } else if (days > 0L) {
            return days + "d";
        } else if (hours > 0L) {
            return hours + "h";
        } else {
            return minutes > 0L ? minutes + "m" : seconds + "s";
        }
    }
}
