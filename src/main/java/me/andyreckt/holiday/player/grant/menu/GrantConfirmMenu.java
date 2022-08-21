package me.andyreckt.holiday.player.grant.menu;

import io.github.zowpy.menu.Button;
import io.github.zowpy.menu.Menu;
import io.github.zowpy.menu.buttons.ConfirmationButton;
import io.github.zowpy.menu.buttons.DisplayButton;
import io.github.zowpy.menu.buttons.EasyButton;
import me.andyreckt.holiday.Holiday;
import me.andyreckt.holiday.database.redis.packet.ProfilePacket;
import me.andyreckt.holiday.player.Profile;
import me.andyreckt.holiday.player.grant.Grant;
import me.andyreckt.holiday.player.rank.Rank;
import me.andyreckt.holiday.utils.CC;
import me.andyreckt.holiday.utils.ItemBuilder;
import me.andyreckt.holiday.utils.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GrantConfirmMenu extends Menu {

    final Profile profile;
    final Rank rank;
    final long time;

    public GrantConfirmMenu(Profile profile, Rank rank, long time) {
        this.profile = profile;
        this.rank = rank;
        this.time = time;
    }


    @Override
    public String getTitle(Player paramPlayer) {
        return "&eConfirm the Grant";
    }

    @Override
    public Map<Integer, Button> getButtons(Player paramPlayer) {
        Map<Integer, Button> toReturn = new HashMap<>();
        String ti = TimeUtil.getDuration(time);

        toReturn.put(13, new DisplayButton(
                new ItemBuilder(Material.PAPER).displayname("&bGrant").lore(
                        CC.MENU_BAR,
                        CC.CHAT + "Player: " + profile.getNameWithColor(),
                        CC.CHAT + "Rank: " + rank.getDisplayName(),
                        CC.CHAT + "Duration: " + CC.PRIMARY + TimeUtil.getDuration(time),
                        CC.MENU_BAR
                ).build()
        ));

        Button confirm = new ConfirmationButton(true, (bool) -> {
            Grant grant = new Grant();
            grant.setTarget(profile.getUuid());
            grant.setIssuedBy(paramPlayer.getUniqueId());
            grant.setIssuedOn(Holiday.getInstance().getServerHandler().getThisServer().getName());
            grant.setIssuedAt(System.currentTimeMillis());
            grant.setRankId(rank.getUuid().toString());
            grant.setDuration(time);
            grant.save();

            paramPlayer.sendMessage(CC.translate("&aYou have granted the rank " + rank.getDisplayName() + " &ato " + profile.getNameWithColor() + " &afor a duration of " + ti));
            Holiday.getInstance().getRedis().sendPacket(new ProfilePacket.ProfileMessagePacket(profile, "&aYou have been granted " + rank.getDisplayName() + "&a for a duration of " + ti));
        } , true);
        Button finallyImFine = new ConfirmationButton(false, (bool) -> paramPlayer.sendMessage(CC.translate("&cCancelled.")), true);

        toReturn.put(0, confirm);
        toReturn.put(1, confirm);
        toReturn.put(2, confirm);

        toReturn.put(9, confirm);
        toReturn.put(10, confirm);
        toReturn.put(11, confirm);

        toReturn.put(18, confirm);
        toReturn.put(19, confirm);
        toReturn.put(20, confirm);

        toReturn.put(6, finallyImFine);
        toReturn.put(7, finallyImFine);
        toReturn.put(8, finallyImFine);

        toReturn.put(15, finallyImFine);
        toReturn.put(16, finallyImFine);
        toReturn.put(17, finallyImFine);

        toReturn.put(24, finallyImFine);
        toReturn.put(25, finallyImFine);
        toReturn.put(26, finallyImFine);

        return toReturn;
    }
}
