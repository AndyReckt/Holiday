package me.andyreckt.holiday.bukkit.server.menu.server;

import lombok.RequiredArgsConstructor;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.CrossServerCommandPacket;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.pagination.PaginatedMenu;
import me.andyreckt.holiday.bukkit.util.other.Utilities;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.server.Server;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ServerListMenu extends PaginatedMenu {

    private final Collection<Server> servers;

    public ServerListMenu(Collection<Server> servers) {
        this.servers = servers;
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "Server List";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        servers.stream()
                .sorted(new ServerComparator())
                .forEach(server -> buttons.put(buttons.size(), new ServerButton(server)));
        return buttons;
    }

    @RequiredArgsConstructor
    private static class ServerButton extends Button {

        private final Server server;

        @Override
        public ItemStack getButtonItem(Player p0) {
            int data = server.isOnline() ? server.isWhitelisted() ? 4 : 5 : 14;
            if (!server.isOnline()) {
                return new ItemBuilder(Material.WOOL)
                        .durability(data)
                        .displayname(CC.SECONDARY + server.getServerName())
                        .lore("", CC.I_RED + "This server is currently offline.")
                        .build();
            }

            StringBuilder sb = new StringBuilder(" ");
            for (double tps : server.getTps()) {
                sb.append(CC.formatTps(tps));
                sb.append(", ");
            }
            String tps = sb.substring(0, sb.length() - 2);
            String status = server.isWhitelisted() ? CC.SECONDARY + "Whitelisted" : CC.GREEN + "Online";

            return new ItemBuilder(Material.WOOL)
                    .durability(data)
                    .displayname(CC.SECONDARY + server.getServerName())
                    .lore(CC.MENU_BAR,
                            CC.CHAT + "ID: " + CC.SECONDARY + server.getServerId(),
                            CC.CHAT + "Status: " + status,
                            CC.CHAT + "Players: " + CC.SECONDARY + server.getOnlinePlayers() + "&7/" + CC.SECONDARY + server.getMaxPlayers(),
                            CC.CHAT + "TPS: " + tps,
                            CC.MENU_BAR)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            switch (clickType) {
                case LEFT:
                case RIGHT:
                    Utilities.sendToServer(player, server.getServerName());
                    break;
                case DROP:
                case CONTROL_DROP:
                    Holiday.getInstance().getApi().getMidnight().sendObject(new CrossServerCommandPacket("stop", server.getServerId()));
                    break;
                default:
                    break;
            }
        }
    }

    private static class ServerComparator implements Comparator<Server> {

        @Override
        public int compare(Server o1, Server o2) {

            if (o1.isOnline() && !o2.isOnline()) {
                return -1;
            }
            if (!o1.isOnline() && o2.isOnline()) {
                return 1;
            }
            if (o1.isWhitelisted() && !o2.isWhitelisted()) {
                return -1;
            }
            if (!o1.isWhitelisted() && o2.isWhitelisted()) {
                return 1;
            }
            if (o1.isOnline() && o2.isOnline()) {
                if (o1.getPlayerCount() > o2.getPlayerCount()) {
                    return -1;
                }
                if (o1.getPlayerCount() < o2.getPlayerCount()) {
                    return 1;
                }
            }

            return o1.getServerName().compareTo(o2.getServerName());
        }
    }
}
