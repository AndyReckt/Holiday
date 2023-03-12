package me.andyreckt.holiday.bukkit.util.sunset.parameter.custom;

import me.andyreckt.holiday.bukkit.util.sunset.parameter.PType;
import me.andyreckt.holiday.core.util.duration.Duration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DurationParameterType implements PType<Duration> {
    @Override
    public Duration transform(CommandSender sender, String string) {
        return Duration.of(string);
    }

    @Override
    public List<String> complete(Player sender, String string) {
        return Arrays.asList("permanent", "1d", "1h", "1m",  "1w", "1y", "3d", "3h", "3m", "3w", "30d");
    }
}
