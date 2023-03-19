package me.andyreckt.holiday.bukkit.server.menu.staff;

import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.DisplayButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.Glass;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.text.DecimalFormat;
import java.util.*;

public class InvSeeMenu extends Menu {

    private final UUID target;
    private Player lastSavedPlayer;

    public InvSeeMenu(Player inv) {
        this.target = inv.getUniqueId();
        this.setAutoUpdate(true);
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "Inventory - " + getTarget().getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        HashMap<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(50, new DisplayButton(new ItemBuilder(Material.PAPER)
                .displayname(CC.PRIMARY + "Infos")
                .lore(CC.SECONDARY + "Player: " + CC.PRIMARY + getTarget().getName())
                .lore(CC.SECONDARY + "Gamemode: " + CC.PRIMARY + getTarget().getGameMode().name().toUpperCase())
                .build()));

        List<ItemStack> contents = new ArrayList<>(Arrays.asList(getTarget().getInventory().getContents()));
        List<ItemStack> armor = new ArrayList<>(Arrays.asList(getTarget().getInventory().getArmorContents()));

        for (int i = 0; i < contents.size(); i++) {
            if (i <= 9) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    toReturn.put(i + 9, new DisplayButton(item, true));
                } else toReturn.put(i + 9, new DisplayButton(new ItemBuilder(Material.AIR).build()));
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
                } else toReturn.put(i + 9, new DisplayButton(new ItemBuilder(Material.AIR).build()));
            }
        }

        for (int i = 0; i < 2; i++) {
            toReturn.put(49 + i, new Glass());
        }
        for (int i = 0; i < 8; i++) {
            toReturn.put(i, new Glass());
        }
        toReturn.put(8, new DisplayButton(getTarget().getItemInHand()));
        for (int i = 0; i < armor.size(); i++) {
            ItemStack item = armor.get(i);
            if (item != null && item.getType() != Material.AIR) {
                toReturn.put(45 + i, new DisplayButton(item));
            } else {
                toReturn.put(45 + i, new Glass());
            }
        }

        List<String> lore = new ArrayList<>();
        if (!getTarget().getActivePotionEffects().isEmpty()) {
            lore.add(CC.translate(CC.MENU_BAR));
            for (PotionEffect effect : getTarget().getActivePotionEffects()) {
                String name = StringUtils.capitalize(effect.getType().getName().replace("_", " ").toLowerCase());
                lore.add(CC.translate(CC.CHAT + name + " " + (effect.getAmplifier() + 1) + "&c for " + CC.CHAT + TimeUtil.niceTime(effect.getDuration() / 20) + "m"));
            }
            lore.add(CC.translate(CC.MENU_BAR));
        }

        ItemStack effects = new ItemBuilder(Material.POTION).displayname(CC.PRIMARY + (getTarget().getActivePotionEffects().isEmpty() ? "No Potion Effects" : getTarget().getActivePotionEffects().size() + " Effect" + (getTarget().getActivePotionEffects().size() == 1 ? "" : "s"))).lore(lore).build();

        String health = new DecimalFormat("0.0").format(getTarget().getHealth());
        int x = (int) Math.round(getTarget().getHealth());

        toReturn.put(51, new DisplayButton(new ItemBuilder(Material.SPECKLED_MELON, x).displayname(CC.PRIMARY + "Health: " + CC.GOLD + health).build()));
        toReturn.put(52, new DisplayButton(new ItemBuilder(Material.PUMPKIN_PIE, getTarget().getFoodLevel() / 2).displayname(CC.PRIMARY + "Saturation: " + CC.GOLD + (getTarget().getFoodLevel() / 2)).build()));
        toReturn.put(53, new DisplayButton(effects));

        return toReturn;
    }

    private Player getTarget() {
        if (Bukkit.getPlayer(target) != null) {
            lastSavedPlayer = Bukkit.getPlayer(target);
        }
        return lastSavedPlayer;
    }


}
