package me.andyreckt.holiday.punishments.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.punishments.PunishData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class PunishmentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public final UUID uuid;
    public final PunishData punishment;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}