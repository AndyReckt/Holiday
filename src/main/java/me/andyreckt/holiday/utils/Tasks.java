package me.andyreckt.holiday.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.andyreckt.holiday.Holiday;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Marko on 25.02.2019.
 */
public class Tasks {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder().setNameFormat(name).build();
    }

    public static void run(Callable callable) {
        Holiday.getInstance().getServer().getScheduler().runTask(Holiday.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Holiday.getInstance().getServer().getScheduler().runTaskAsynchronously(Holiday.getInstance(), callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Holiday.getInstance().getServer().getScheduler().runTaskLater(Holiday.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Holiday.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Holiday.getInstance(), callable::call, delay);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Holiday.getInstance().getServer().getScheduler().runTaskTimer(Holiday.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Holiday.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Holiday.getInstance(), callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }
}
