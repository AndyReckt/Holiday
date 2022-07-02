package me.andyreckt.holiday.server.menu.button;

import io.github.zowpy.menu.Button;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ServerButton extends Button {

    private final Server serverData;

    public ServerButton(Server serverData) {
        this.serverData = serverData;
    }

    @Override
    public ItemStack getButtonItem(Player p0) {
        ItemBuilder ib = new ItemBuilder(Material.WOOL, CC.SECONDARY + serverData.getName());

        String status = serverData.isWhitelisted() ? CC.PRIMARY + "Whitelisted" : "&aOnline";
        int data = serverData.isWhitelisted() ? 2 : 5;

        ib.durability(data);
        ib.lore(CC.MENU_BAR,
                CC.CHAT + "Players: " + CC.PRIMARY + serverData.getPlayers(),
                CC.CHAT + "Max Players: " + CC.PRIMARY + serverData.getMaxplayers(),
                CC.CHAT + "Status: " + status,
                " ",
                "&7&oClick to connect...",
                CC.MENU_BAR);

        return ib.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        player.chat("/join " + serverData.getName());
    }
}
