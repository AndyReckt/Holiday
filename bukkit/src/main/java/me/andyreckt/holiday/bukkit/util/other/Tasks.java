package me.andyreckt.holiday.bukkit.util.other;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.andyreckt.holiday.bukkit.Holiday;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marko on 25.02.2019.
 */
public class Tasks {

    public static void run(Callable callable) {
        Holiday.getInstance().getServer().getScheduler().runTask(Holiday.getInstance(), callable::call);
    }

    public static void runAsync(Callable callable) {
        Holiday.getInstance().getExecutor().execute(callable::call);
    }

    public static void runLater(Callable callable, long delay) {
        Holiday.getInstance().getServer().getScheduler().runTaskLater(Holiday.getInstance(), callable::call, delay);
    }

    public static void runAsyncLater(Callable callable, long delay) {
        Holiday.getInstance().getScheduledExecutor().schedule(callable::call, delay * 50L, TimeUnit.MILLISECONDS);
    }

    public static void runTimer(Callable callable, long delay, long interval) {
        Holiday.getInstance().getServer().getScheduler().runTaskTimer(Holiday.getInstance(), callable::call, delay, interval);
    }

    public static void runAsyncTimer(Callable callable, long delay, long interval) {
        Holiday.getInstance().getScheduledExecutor().scheduleAtFixedRate(callable::call, delay * 50L, interval * 50L, TimeUnit.MILLISECONDS);
    }

    public interface Callable {
        void call();
    }
}
