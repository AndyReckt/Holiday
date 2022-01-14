package me.andyreckt.holiday.player.staff.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class StaffModeEnterEvent extends PlayerEvent {
   static HandlerList handlerList = new HandlerList();

   public StaffModeEnterEvent(Player player) {
      super(player);
   }

   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
