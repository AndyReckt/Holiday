package io.github.damt.menu;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public enum MenuType {

    HOPPER {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.HOPPER, menu.getTitle());
        }
    },

    INVENTORY {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, menu.getSize(), menu.getTitle());
        }
    },

    FURNACE {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.FURNACE, menu.getTitle());
        }
    },

    BREWING_STAND {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.BREWING, menu.getTitle());
        }
    },

    ENCHANTING {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.ENCHANTING, menu.getTitle());
        }
    },

    BEACON {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.BEACON, menu.getTitle());
        }
    },

    CRAFTING {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.CRAFTING, menu.getTitle());
        }
    },

    DISPENSER {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.DISPENSER, menu.getTitle());
        }
    },

    DROPPER {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.DROPPER, menu.getTitle());
        }
    },

    MERCHANT {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.MERCHANT, menu.getTitle());
        }
    },

    ANVIL {
        @Override
        public Inventory createInventory(Menu menu) {
            return Bukkit.createInventory(null, InventoryType.ANVIL, menu.getTitle());
        }
    };

    /**
     * Create a new inventory with the menu type
     *
     * @param menu the menu to create it for
     * @return the inventory
     */
    public abstract Inventory createInventory(Menu menu);

}
