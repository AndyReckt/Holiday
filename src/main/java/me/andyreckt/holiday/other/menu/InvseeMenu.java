package me.andyreckt.holiday.other.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.DisplayButton;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.TimeUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.text.DecimalFormat;
import java.util.*;

public class InvseeMenu extends Menu {

    public static HashMap<Player, Player> invMap = new HashMap<>();

    public InvseeMenu(Player player, Player inv) {
        InvseeMenu.invMap.put(player, inv);
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return CC.BLUE + "Inventory of " + invMap.get(paramPlayer).getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        HashMap<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(50, new DisplayButton(new ItemBuilder(Material.PAPER).displayname(CC.RED + "Infos").lore(CC.YELLOW + "Player: " + CC.RED + InvseeMenu.invMap.get(paramPlayer).getName()).lore(CC.YELLOW + "Gamemode: " + CC.RED + InvseeMenu.invMap.get(paramPlayer).getGameMode().name().toUpperCase()).build()));

        List<ItemStack> contents = new ArrayList<>(Arrays.asList(InvseeMenu.invMap.get(paramPlayer).getInventory().getContents()));
        List<ItemStack> armor = new ArrayList<>(Arrays.asList(InvseeMenu.invMap.get(paramPlayer).getInventory().getArmorContents()));

        for (int i = 0; i < contents.size(); i++) {
            if (i <= 9) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    toReturn.put(i + 9, new DisplayButton(item, true));
                }
            }
        }

        for (int i = 0; i < contents.size(); i++) {
            if (i > 9) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    int position = i + 9;

                    while (toReturn.get(position) != null) {
                        position++;
                        if (position == toReturn.size()) break;
                    }

                    if (position != toReturn.size()) {
                        toReturn.put(position, new DisplayButton(new ItemBuilder(Material.AIR).build()));
                        toReturn.put(position, new DisplayButton(item));
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            toReturn.put(49 + i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).displayname(" ").build()));
        }
        for (int i = 0; i < 8; i++) {
            toReturn.put(i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).displayname(" ").build()));
        }
        toReturn.put(8, new DisplayButton(InvseeMenu.invMap.get(paramPlayer).getItemInHand()));
        for (int i = 0; i < armor.size(); i++) {
            ItemStack item = armor.get(i);
            if (item != null && item.getType() != Material.AIR) {
                toReturn.put(45 + i, new DisplayButton(item));
            } else {
                toReturn.put(45 + i, new DisplayButton(new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).displayname(" ").build()));
            }
        }

        List<String> lore = new ArrayList<>();
        if (!InvseeMenu.invMap.get(paramPlayer).getActivePotionEffects().isEmpty()) {
            lore.add(CC.translate(CC.MENU_BAR));
            for (PotionEffect effect : InvseeMenu.invMap.get(paramPlayer).getActivePotionEffects()) {
                String name = WordUtils.capitalize(effect.getType().getName().replace("_", " ").toLowerCase());
                lore.add(CC.translate("&e" + name + " " + (effect.getAmplifier() + 1) + "&c for &e" + TimeUtil.niceTime(effect.getDuration() / 20) + "m"));
            }
            lore.add(CC.translate(CC.MENU_BAR));
        }

        ItemStack effects = new ItemBuilder(Material.POTION).displayname(CC.RED + (InvseeMenu.invMap.get(paramPlayer).getActivePotionEffects().isEmpty() ? "No Potion Effects" : InvseeMenu.invMap.get(paramPlayer).getActivePotionEffects().size() + " Effect" + (InvseeMenu.invMap.get(paramPlayer).getActivePotionEffects().size() == 1 ? "" : "s"))).lore(lore).build();

        String health = new DecimalFormat("0.0").format(InvseeMenu.invMap.get(paramPlayer).getHealth());
        toReturn.put(51, new DisplayButton(new ItemBuilder(Material.APPLE, Integer.parseInt(health.split("\\.")[0])).displayname(CC.RED + "Health: " + CC.GOLD + health).build()));
        toReturn.put(52, new DisplayButton(new ItemBuilder(Material.PUMPKIN_PIE, InvseeMenu.invMap.get(paramPlayer).getFoodLevel() / 2).displayname(CC.RED + "Saturation: " + CC.GOLD + (InvseeMenu.invMap.get(paramPlayer).getFoodLevel() / 2)).build()));
        toReturn.put(53, new DisplayButton(effects));


        return toReturn;
    }


}
