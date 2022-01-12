package me.andyreckt.holiday.server.menu.button;

import io.github.damt.menu.buttons.Button;
import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerButton extends Button {

    public ServerButton(Server serverData) {
        super(Material.WOOL);
        this.setItemStack(getItemStack(serverData));
    }


    public boolean isEditable(@NonNull Player player) {
        return false;
    }

    public ItemStack getItemStack(Server serverData) {

        ItemBuilder ib = new ItemBuilder(Material.WOOL, "&5" + serverData.getName());

        String status = serverData.isWhitelisted() ? "&dWhitelisted" : "&aOnline";
        int data = serverData.isWhitelisted() ? 2 : 5;

        ib.durability(data);
        ib.lore(CC.MENU_BAR,
                "&ePlayers: &d" + serverData.getPlayers(),
                "&eMax Players: &d" + serverData.getMaxplayers(),
                "&eStatus: " + status,
                CC.MENU_BAR);

        return ib.build();
    }

}
