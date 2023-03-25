package me.andyreckt.holiday.bukkit.util.menu.pagination;

import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.BackButton;
import me.andyreckt.holiday.bukkit.util.menu.buttons.Glass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public abstract class PaginatedMenu extends Menu {
    private int page;
    private int glassColor;

    public PaginatedMenu() {
        this.page = 1;
        this.setUpdateAfterClick(false);
    }

    public int getGlassColor() {
        return this.glassColor;
    }

    public void setGlassColor(int glassColor) {
        this.glassColor = glassColor;
    }

    @Override
    public String getTitle(final Player player) {
        return ChatColor.translateAlternateColorCodes('&', this.getPrePaginatedTitle(player) + " &8(&7" + this.page + "/" + this.getPages(player) + "&8)");
    }

    public final void modPage(final Player player, final int mod) {
        this.page += mod;
        this.getButtons().clear();
        this.openMenu(player);
    }

    public final int getPages(final Player player) {
        final int buttonAmount = this.getAllPagesButtons(player).size();
        if (buttonAmount == 0) {
            return 1;
        }
        return (int) Math.ceil(buttonAmount / (double) this.getMaxItemsPerPage(player));
    }

    @Override
    public final Map<Integer, Button> getButtons(final Player player) {
        final int minIndex = (int) ((this.page - 1) * (double) this.getMaxItemsPerPage(player));
        final int maxIndex = (int) (this.page * (double) this.getMaxItemsPerPage(player));
        final HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i : new int[]{0, 1, 7, 8, 9, 17, 27, 35, 36, 37, 43, 44}) {
            buttons.put(i, new Glass(getGlassColor()));
        }

        PageButton prevPage = new PageButton(-1, this);
        PageButton nextPage = new PageButton(1, this);

        if (prevPage.hasNext(player)) {
            buttons.put(39, prevPage);
        }

        if (nextPage.hasNext(player)) {
            buttons.put(41, nextPage);
        }

        for (final Map.Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
            int ind = entry.getKey();
            if (ind >= minIndex && ind < maxIndex) {

                ind -= ((this.page - 1) * 21);

                switch (ind / 7) {
                    case 0:
                        ind += 10;
                        break;
                    case 1:
                        ind += 10 + 2;
                        break;
                    case 2:
                        ind += 10 + 4;
                        break;
                }

//                System.out.println(entry.getKey() + " " + ind);
//                if (ind >= 17 && ind <= 24) {
//                    ind += 2;
//                } else if (ind >= 25 && ind <= 36) {
//                    ind += 4;
//                }
//                ind -= (int) (this.getMaxItemsPerPage(player) * (double) (this.page - 1)) - 10;
//                System.out.println(entry.getKey() + " " + ind);

                buttons.put(ind, entry.getValue());
            }
        }
        final Map<Integer, Button> global = this.getGlobalButtons(player);
        if (global != null) {
            buttons.putAll(global);
        }

        if (backButton() != null) {
            buttons.put(40, new BackButton(this.backButton())); //getSize() - 5
        }

        return buttons;
    }

    public int getMaxItemsPerPage(final Player player) {
        return 21;
    }

    public Map<Integer, Button> getGlobalButtons(final Player player) {
        return null;
    }

    public abstract String getPrePaginatedTitle(final Player p0);

    public abstract Map<Integer, Button> getAllPagesButtons(final Player p0);

    public Menu backButton() {
        return null;
    }

    @Override
    protected void surroundButtons(boolean full, Map<Integer, Button> buttons, final ItemStack itemStack) {
        IntStream.range(0, getSize()).filter(slot -> (buttons.get(slot) == null)).forEach(slot -> {
            if (slot < 9 || slot > getSize() - 10 || (full && (slot % 9 == 0 || (slot + 1) % 9 == 0)))
                buttons.put(slot, new Button() {
                    public ItemStack getButtonItem(Player player) {
                        return itemStack;
                        }
                });
        });
    }

    public int getPage() {
        return this.page;
    }
}
