package me.andyreckt.holiday.bukkit.server.menu.disguise;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.anvilgui.AnvilGUI;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.core.util.http.Skin;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DisguiseSkinMenu extends PaginatedMenu {

    private final Disguise disguise;

    public DisguiseSkinMenu(Disguise disguise) {
        this.disguise = disguise;
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Disguise Skins";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Skin skin : Skin.SKINS.values()) {
            buttons.put(buttons.size(), new EasyButton(
                    new ItemBuilder(Material.SKULL_ITEM)
                            .durability(SkullType.PLAYER.ordinal())
                            .texture(skin.getValue())
                            .displayname(CC.CHAT + skin.getName())
                            .build(), o -> {
                        disguise.setSkinName(skin.getName());
                        new DisguiseMenu(disguise).openMenu(p0);
            }));
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(18, new EasyButton(new ItemBuilder(Material.SKULL_ITEM)
                .durability(SkullType.PLAYER.ordinal())
                .owner(disguise.getDisplayName())
                .displayname(CC.SECONDARY + "Disguise as " + disguise.getDisplayName())
                .build(), o -> {
            disguise.setSkinName(disguise.getDisplayName());
            new DisguiseMenu(disguise).openMenu(player);
        }));

        buttons.put(26, new EasyButton(
                new ItemBuilder(Material.ANVIL)
                        .displayname(CC.PRIMARY + "Enter skin").build(), o -> {
            new AnvilGUI.Builder()
                    .text("Skin Name")
                    .title("Enter skin")
                    .onComplete((player1, text) -> {
                        if (text.contains(" ")) {
                            player1.sendMessage(Locale.INVALID_NAME.getString());
                            this.openMenu(player);
                            return AnvilGUI.Response.close();
                        }
                        disguise.setSkinName(text);
                        new DisguiseMenu(disguise).openMenu(player1);
                        return AnvilGUI.Response.close();
                    })
                    .onClose(player1 -> {
                        new DisguiseMenu(disguise).openMenu(player1);
                    })
                    .plugin(Holiday.getInstance())
                    .open(player);
        }));

        return buttons;
    }

    @Override
    public Menu backButton() {
        return new DisguiseMenu(disguise);
    }
}
