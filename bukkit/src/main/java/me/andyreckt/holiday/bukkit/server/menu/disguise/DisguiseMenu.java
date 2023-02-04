package me.andyreckt.holiday.bukkit.server.menu.disguise;

import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.user.UserConstants;
import me.andyreckt.holiday.core.user.disguise.Disguise;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.files.Perms;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import me.andyreckt.holiday.bukkit.util.menu.anvilgui.AnvilGUI;
import me.andyreckt.holiday.bukkit.util.menu.buttons.DisplayButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.EasyButton;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import me.andyreckt.holiday.core.util.http.Skin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DisguiseMenu extends GlassMenu {

    private final Disguise disguise;

    public DisguiseMenu(Disguise disguise) {
        this.disguise = disguise;
    }

    @Override
    public int getGlassColor() {
        return 6;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new DisplayButton(
                new ItemBuilder(Material.SKULL_ITEM)
                        .displayname(CC.PRIMARY + disguise.getDisplayName())
                        .lore("",
                                CC.CHAT + "Skin: " + CC.SECONDARY + disguise.getSkinName(),
                                CC.CHAT + "Rank: " + CC.SECONDARY + disguise.getDisguiseRank().getDisplayName(),
                                "")
                        .durability(SkullType.PLAYER.ordinal())
                        .texture(disguise.getSkin().getValue())
                        .build()
        ));
        if (player.hasPermission(Perms.DISGUISE_CUSTOM_NAME.get())) {
            buttons.put(11, new EasyButton(
                    new ItemBuilder(Material.ANVIL)
                            .displayname(CC.PRIMARY + "Change Name").build(), o -> {
                    new AnvilGUI.Builder()
                        .text("Name")
                        .title("Change Name")
                        .onComplete((player1, text) -> {
                            if (!UserConstants.DISGUISE_NAME_MATCHER.matches(text)) {
                                player1.sendMessage(Locale.INVALID_NAME.getString());
                                return AnvilGUI.Response.text(disguise.getDisplayName());
                            }

                            if (!Holiday.getInstance().getDisguiseManager().isValidName(text)) {
                                player1.sendMessage(Locale.DISGUISE_NAME_TAKEN.getString());
                                return AnvilGUI.Response.text(disguise.getDisplayName());
                            }

                            disguise.setDisplayName(text);
                            new DisguiseMenu(disguise).openMenu(player1);
                            return AnvilGUI.Response.close();
                        })
                        .onClose(player1 -> {
                            new DisguiseMenu(disguise).openMenu(player1);
                        })
                        .plugin(Holiday.getInstance())
                        .open(player);
            }));
        }
        buttons.put(13, new EasyButton(
                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .displayname(CC.PRIMARY + "Change Rank").build(),
                o -> new DisguiseRankMenu(disguise).openMenu(player)));
        buttons.put(15, new EasyButton(
                new ItemBuilder(Material.SKULL_ITEM)
                        .durability(SkullType.PLAYER.ordinal())
                        .displayname(CC.PRIMARY + "Change Skin")
                        .texture(Skin.getSkinByName("anon").join().getValue())
                        .build(),
                o -> new DisguiseSkinMenu(disguise).openMenu(player)));
        buttons.put(16, new EasyButton(
                new ItemBuilder(Material.INK_SACK)
                        .displayname(CC.GREEN + "Confirm")
                        .durability(StringUtil.convertChatColorToDyeData(ChatColor.GREEN))
                        .build(),
                o -> {
                    player.closeInventory();
                    Holiday.getInstance().getDisguiseManager().disguise(disguise, true);
                    String toSend = Locale.DISGUISE_MESSAGE.getString()
                            .replace("%name%", UserConstants.getDisplayNameWithColor(Holiday.getInstance().getApi().getProfile(o.getUniqueId())))
                            .replace("%skin%", disguise.getSkinName());
                    player.sendMessage(toSend);
                }));

        return buttons;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "Disguise Menu";
    }
}
