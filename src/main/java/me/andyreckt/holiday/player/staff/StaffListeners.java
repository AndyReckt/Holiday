package me.andyreckt.holiday.player.staff;

import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.StaffMessages;
import me.andyreckt.holiday.other.enums.StaffMessageType;
import me.andyreckt.holiday.other.menu.InvseeMenu;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.staff.event.StaffUpdateVisibilityEvent;
import me.andyreckt.holiday.player.staff.menu.StaffListMenu;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;


public class StaffListeners {

    public StaffListeners(Holiday holiday) {
        holiday.getServer().getPluginManager().registerEvents(new ModListeners(), holiday);
        holiday.getServer().getPluginManager().registerEvents(new FreezeListeners(), holiday);
        holiday.getServer().getPluginManager().registerEvents(new ModItems(), holiday);
    }

    public static class ModListeners implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void staffChat(AsyncPlayerChatEvent event) {
            Profile profile = Holiday.getInstance().getProfileHandler().getByPlayer(event.getPlayer());

            if (!profile.isStaff()) return;
            if (!event.getMessage().startsWith(Holiday.getInstance().getSettings().getString("SERVER.STAFFCHATPREFIX"))) return;

            String message = event.getMessage().substring(1);
            String playerName = profile.getNameWithColor();
            String server = Holiday.getInstance().getSettings().getString("SERVER.NICENAME");
            Holiday.getInstance().getRedis().sendPacket(new StaffMessages.StaffMessagesPacket(
                    Holiday.getInstance().getMessages().getString("STAFF.CHAT").replace("<server>", server).replace("<player>", playerName).replace("<message>", message),
                    StaffMessageType.STAFF
            ));
            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void staffUpdateVis(StaffUpdateVisibilityEvent event) {
            if(event.isCancelled()) return;

            StaffHandler sh = Holiday.getInstance().getStaffHandler();

            if(!sh.isInStaffMode(event.getPlayer().getUniqueId())) return;

            Player player = event.getPlayer();
            StaffHandler.StaffData sPlayer = sh.getStaffPlayer(player);

            if (!Holiday.getInstance().getSettings().getBoolean("STAFFMODE.UPDATEVISIBILITY")) {
                event.setCancelled(true);
                return;
            }

            if(sPlayer.isInVanish()) {
                player.spigot().setCollidesWithEntities(false);
                Bukkit.getOnlinePlayers().stream().filter(p -> sh.canSee(p, player)).forEach(p -> p.showPlayer(player));
                Bukkit.getOnlinePlayers().stream().filter(p -> !sh.canSee(p, player)).forEach(p -> p.hidePlayer(player));
            } else {
                Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
                player.spigot().setCollidesWithEntities(true);
            }
        }

        @EventHandler
        public void onJoinSetStaffMode(PlayerJoinEvent event) {
            Tasks.runLater(() -> {
                if(Bukkit.getPlayer(event.getPlayer().getUniqueId()) != null) {
                    Profile p = Holiday.getInstance().getProfileHandler().getByPlayer(event.getPlayer());
                    if(p.isInStaffMode()) Holiday.getInstance().getStaffHandler().init(event.getPlayer());
                }
            }, 10L);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onLeaveStaffDestroy(PlayerQuitEvent event) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if(sh.isInStaffMode(event.getPlayer().getUniqueId())) sh.destroyWithoutSave(event.getPlayer());
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onKickStaffDestroy(PlayerKickEvent event) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if(sh.isInStaffMode(event.getPlayer().getUniqueId())) sh.destroyWithoutSave(event.getPlayer());
        }

        @EventHandler
        public void onFood(FoodLevelChangeEvent event) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(event.getEntity().getUniqueId()))
            event.setCancelled(true);
            ((Player) event.getEntity()).setSaturation(20);
            ((Player) event.getEntity()).setFoodLevel(20);
        }

        @EventHandler
        public void onItemDrop(PlayerDropItemEvent e) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getPlayer().getUniqueId())) return;
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBlockPlace(BlockPlaceEvent e) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getPlayer().getUniqueId())) return;
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onBlockBreak(BlockBreakEvent e) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getPlayer().getUniqueId())) return;
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onItemPickup(PlayerPickupItemEvent e) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getPlayer().getUniqueId())) return;
            e.setCancelled(true);
            e.getPlayer().updateInventory();
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getWhoClicked().getUniqueId())) return;
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        }

        @EventHandler
        public void onDamageTake(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Player)) return;
            Player player = (Player) e.getEntity();
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(player.getUniqueId())) return;
            e.setCancelled(true);
        }
        @EventHandler
        public void onDamageDealt(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player)) return;
            Player player = (Player) e.getDamager();
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(player.getUniqueId())) return;
            if (!sh.getStaffPlayer(player).isInVanish()) return;
            e.setCancelled(true);
        }
    }

    public static class FreezeListeners implements Listener { // Do not mind this pyramidal code i was too lazy to fix it lmao
        @EventHandler
        public void onAttack(EntityDamageEvent event) {
            Entity p = event.getEntity();
            if (p instanceof Player) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onDamage(EntityDamageByEntityEvent event) {
            Entity p = event.getDamager();
            if (p instanceof Player) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onDrop(PlayerDropItemEvent event) {
            Player p = event.getPlayer();
            if (p != null) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onPickup(PlayerPickupItemEvent event) {
            Player p = event.getPlayer();
            if (p != null) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onPlace(BlockPlaceEvent event) {
            Player p = event.getPlayer();
            if (p != null) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onBreak(BlockBreakEvent event) {
            Player p = event.getPlayer();
            if (p != null) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }
        @EventHandler
        public void onClick(InventoryClickEvent event) {
            Player p = (Player) event.getWhoClicked();
            if (p != null) {
                if (p.hasMetadata("frozen")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public static class ModItems implements Listener {
        @EventHandler
        public void onClick(PlayerInteractEvent e) {

            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getPlayer().getUniqueId())) return;

            Player player = e.getPlayer();

            ItemStack item = e.getItem();
            if(item == null) return;
            if(item.isSimilar(Items.GO_INVIS) || item.isSimilar(Items.GO_VIS)) {
                sh.getStaffPlayer(player).vanish();
            }
            if (item.isSimilar(Items.STAFF_LIST)) {
                new StaffListMenu().openMenu(player);
            }
        }

        @EventHandler
        public void onEntityClick(PlayerInteractEntityEvent e) {
            StaffHandler sh = Holiday.getInstance().getStaffHandler();
            if (!sh.isInStaffMode(e.getPlayer().getUniqueId())) return;

            Player player = e.getPlayer();

            ItemStack item = e.getPlayer().getItemInHand();

            if (!(e.getRightClicked() instanceof Player)) return;

            Player clicked = (Player) e.getRightClicked();
            if(item == null) return;

            if(item.isSimilar(Items.INVSEE)) {
                new InvseeMenu(e.getPlayer(), clicked).openMenu(e.getPlayer());
            }
            else if(item.isSimilar(Items.FREEZE)) {
                sh.handleFreeze(clicked);

                if (clicked.hasMetadata("frozen")) {
                    player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("FREEZE.STAFF.FROZEN").replace("<player>", clicked.getName())));
                } else {
                    player.sendMessage(CC.translate(Holiday.getInstance().getMessages().getString("FREEZE.STAFF.UNFROZEN").replace("<player>", clicked.getName())));
                }
            }
        }
    }
}
