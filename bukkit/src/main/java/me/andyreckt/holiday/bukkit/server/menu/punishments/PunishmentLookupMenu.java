package me.andyreckt.holiday.bukkit.server.menu.punishments;

import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.api.user.IPunishment;
import me.andyreckt.holiday.bukkit.server.menu.punishments.check.button.PunishmentCheckButton;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PunishmentLookupMenu extends GlassMenu {

    private final IPunishment punishment;

    @Override
    public int getGlassColor() {
        return 0;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(13, new PunishmentCheckButton(punishment));

        return buttons;
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "Lookup " + punishment.getId();
    }
}
