package me.andyreckt.holiday.server.menu.button;

import me.andyreckt.holiday.server.Server;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import com.ericstolly.menu.button.MenuButton;
import com.ericstolly.menu.button.listener.MenuButtonListener;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerButton extends MenuButton {

    final Server serverData;

    public ServerButton(Server serverData) {
        this.serverData = serverData;
    }

    @Override
    public boolean isEditable(@NonNull Player player) {
        return false;
    }

    @Override
    public ItemStack getItemStack(@NonNull Player player) {

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

    @Override
    public MenuButtonListener getButtonListener(@NonNull Player player) {
        return null;
    }
}
