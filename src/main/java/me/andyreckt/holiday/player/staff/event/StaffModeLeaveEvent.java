package me.andyreckt.holiday.player.staff.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class StaffModeLeaveEvent extends PlayerEvent {
   static HandlerList handlerList = new HandlerList();

   public StaffModeLeaveEvent(Player player) {
      super(player);
   }

   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
