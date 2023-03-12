package me.andyreckt.holiday.bukkit.server.menu.grant;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.text.StringUtil;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GrantChooseTimeMenu extends Menu {

    private final Profile profile;
    private final IRank rank;

    public GrantChooseTimeMenu(Profile profile, IRank rank) {
        this.profile = profile;
        this.rank = rank;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "&bChoose a duration";
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();
        toReturn.put(0, new TimeButton(ChatColor.GOLD, "15m", profile, rank));
        toReturn.put(1, new TimeButton(ChatColor.YELLOW, "1h", profile, rank));
        toReturn.put(2, new TimeButton(ChatColor.AQUA, "12h", profile, rank));
        toReturn.put(3, new TimeButton(ChatColor.LIGHT_PURPLE, "1d", profile, rank));
        toReturn.put(4, new TimeButton(ChatColor.DARK_PURPLE, "3d", profile, rank));
        toReturn.put(5, new TimeButton(ChatColor.RED, "7d", profile, rank));
        toReturn.put(6, new TimeButton(ChatColor.DARK_GRAY, "14d", profile, rank));
        toReturn.put(7, new TimeButton(ChatColor.DARK_AQUA, "30d", profile, rank));
        toReturn.put(8, new TimeButton(ChatColor.BLUE, "Permanent", profile, rank));
        return toReturn;
    }

    static class TimeButton extends Button {

        private final ChatColor color;
        private final String time;
        private final Profile profile;
        private final IRank rank;

        public TimeButton(ChatColor color, String time, Profile profile, IRank rank) {
            this.color = color;
            this.time = time;
            this.profile = profile;
            this.rank = rank;
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            return new ItemBuilder(Material.WOOL)
                    .displayname(color + Duration.of(time).getFormatted())
                    .damage(StringUtil.convertChatColorToWoolData(color))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new GrantChooseReasonMenu(profile, rank, Duration.of(time)).openMenu(player);
        }
    }
}
