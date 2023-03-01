package me.andyreckt.holiday.bukkit.util.sunset.parameter.custom;

import me.andyreckt.holiday.bukkit.util.sunset.parameter.PType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDParameterType implements PType<UUID> {
    @Override
    public UUID transform(CommandSender sender, String string) {
        return UUID.fromString(string);
    }

    @Override
    public List<String> complete(Player sender, String string) {
        return new ArrayList<>();
    }
}
