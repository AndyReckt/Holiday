package me.andyreckt.holiday.bukkit.server.menu.grant;

import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.server.redis.packet.PlayerMessagePacket;
import me.andyreckt.holiday.bukkit.util.files.Locale;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.menu.GlassMenu;
import me.andyreckt.holiday.bukkit.util.menu.Menu;
import me.andyreckt.holiday.bukkit.util.menu.buttons.ConversationButton;
import me.andyreckt.holiday.bukkit.util.menu.pagination.ConfirmationMenu;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.grant.Grant;
import me.andyreckt.holiday.core.util.duration.Duration;
import me.andyreckt.holiday.core.util.duration.TimeUtil;
import me.andyreckt.holiday.core.util.redis.messaging.PacketHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GrantChooseReasonMenu extends GlassMenu {

    private final Profile profile;
    private final IRank rank;
    private final Duration duration;

    public GrantChooseReasonMenu(Profile profile, IRank rank, Duration duration) {
        this.profile = profile;
        this.rank = rank;
        this.duration = duration;
    }

    @Override
    public int getGlassColor() {
        return 7;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> toReturn = new HashMap<>();
        toReturn.put(10, new ReasonButton("Promoted", profile, rank, duration));
        toReturn.put(11, new ReasonButton("Demoted", profile, rank, duration));
        toReturn.put(12, new ReasonButton("Store", profile, rank, duration));
        toReturn.put(13, new ReasonButton("Addon", profile, rank, duration));
        toReturn.put(14, new ReasonButton("Famous", profile, rank, duration));
        toReturn.put(15, new ReasonButton("Issue", profile, rank, duration));
        toReturn.put(16, new ConversationButton<>(
                new ItemBuilder(Material.BOOK)
                        .displayname(CC.SECONDARY + "Other")
                        .build(),
                null, Locale.GRANT_REASON.getString(),
                (x, pair) -> new ConfirmationMenu(() -> {
                    Grant grant = new Grant(profile.getUuid(),
                            rank,
                            player.getUniqueId(),
                            pair.getB(),
                            Holiday.getInstance().getThisServer().getServerName(),
                            duration);
                    Holiday.getInstance().getApi().saveGrant(grant);
                    String str = Locale.GRANT_TARGET.getString()
                            .replace("%rank%", rank.getDisplayName())
                            .replace("%duration%", duration.getFormatted())
                            .replace("%reason%", pair.getB());
                    String str2 = Locale.GRANT_PLAYER.getString()
                            .replace("%player%", profile.getName())
                            .replace("%rank%", rank.getDisplayName())
                            .replace("%duration%", duration.getFormatted())
                            .replace("%reason%", pair.getB());
                    player.sendMessage(CC.translate(str2));
                    PacketHandler.send(new PlayerMessagePacket(profile.getUuid(), str));

                },
                        new ItemBuilder(Material.ENCHANTED_BOOK)
                                .displayname(CC.SECONDARY + "Confirm Grant")
                                .lore("",
                                        CC.SECONDARY + "Rank: " + CC.PRIMARY + rank.getDisplayName(),
                                        CC.SECONDARY + "Duration: " + CC.PRIMARY + duration.toRoundedTime(),
                                        CC.SECONDARY + "Reason: " + CC.PRIMARY + pair.getB(),
                                        "",
                                        CC.SECONDARY + "Click to confirm.")
                                .build(),
                        new GrantChooseReasonMenu(profile, rank, duration)
                ).openMenu(player)
        ));
        return toReturn;
    }

    @Override
    public Menu backButton() {
        return new GrantChooseTimeMenu(profile, rank);
    }

    @Override
    public String getTitle(Player paramPlayer) {
        return "&bChoose a reason";
    }

    static class ReasonButton extends Button {

        private final String reason;
        private final Profile profile;
        private final IRank rank;
        private final Duration duration;

        public ReasonButton(String reason, Profile profile, IRank rank, Duration duration) {
            this.reason = reason;
            this.profile = profile;
            this.rank = rank;
            this.duration = duration;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new ConfirmationMenu(() -> {
                Grant grant = new Grant(profile.getUuid(),
                        rank,
                        player.getUniqueId(),
                        reason,
                        Holiday.getInstance().getThisServer().getServerName(),
                        duration);
                Holiday.getInstance().getApi().saveGrant(grant);
                String str = Locale.GRANT_TARGET.getString()
                        .replace("%rank%", rank.getDisplayName())
                        .replace("%duration%", duration.getFormatted())
                        .replace("%reason%", reason);
                String str2 = Locale.GRANT_PLAYER.getString()
                        .replace("%player%", profile.getName())
                        .replace("%rank%", rank.getDisplayName())
                        .replace("%duration%", duration.getFormatted()
                        .replace("%reason%", reason));
                player.sendMessage(CC.translate(str2));
                PacketHandler.send(new PlayerMessagePacket(profile.getUuid(), str));

            },
                new ItemBuilder(Material.ENCHANTED_BOOK)
                        .displayname(CC.SECONDARY + "Confirm Grant")
                        .lore("",
                                CC.SECONDARY + "Rank: " + CC.PRIMARY + rank.getDisplayName(),
                                CC.SECONDARY + "Duration: " + CC.PRIMARY + duration.toRoundedTime(),
                                CC.SECONDARY + "Reason: " + CC.PRIMARY + reason,
                                "",
                                CC.SECONDARY + "Click to confirm.")
                        .build(),
                    new GrantChooseReasonMenu(profile, rank, duration)
            ).openMenu(player);
        }

        @Override
        public ItemStack getButtonItem(Player p0) {
            return new ItemBuilder(Material.BOOK)
                    .displayname(CC.SECONDARY + reason)
                    .build();
        }
    }

}
