package me.andyreckt.holiday.player.staff.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class StaffUpdateVisibilityEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlerList = new HandlerList();
   private static boolean cancelled = false;

   public StaffUpdateVisibilityEvent(Player player) {
      super(player);
   }

   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   @Override
   public boolean isCancelled() {
      return cancelled;
   }

   @Override
   public void setCancelled(boolean b) {
      cancelled = b;
   }
}
