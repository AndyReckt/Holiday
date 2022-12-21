package me.andyreckt.holiday.bukkit.user.disguise;

import lombok.*;
import me.andyreckt.holiday.api.user.IDisguise;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.player.Skin;
import me.andyreckt.holiday.core.user.rank.Rank;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Disguise implements IDisguise {

    private final UUID uuid;
    private String displayName;
    private String skinName;
    private UUID disguiseRank;


    public Disguise(UUID uuid) {
        this.uuid = uuid;
        this.displayName = Holiday.getInstance().getDisguiseManager().getRandomName();
        this.skinName = Holiday.getInstance().getDisguiseManager().getRandomSkin().getName();
        this.disguiseRank = Holiday.getInstance().getApi().getDefaultRank().getUuid();
    }

    @Override
    public IRank getDisguiseRank() {
        return Holiday.getInstance().getApi().getRank(disguiseRank);
    }

    @Override
    public void setDisguiseRank(IRank disguiseRank) {
        this.disguiseRank = disguiseRank.getUuid();
    }

    public Skin getSkin() {
        return Skin.getSkinByName(skinName).join();
    }
}
