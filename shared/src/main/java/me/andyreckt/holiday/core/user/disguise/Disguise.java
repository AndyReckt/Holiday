package me.andyreckt.holiday.core.user.disguise;

import lombok.*;
import me.andyreckt.holiday.api.user.IDisguise;
import me.andyreckt.holiday.api.user.IRank;
import me.andyreckt.holiday.core.HolidayAPI;
import me.andyreckt.holiday.core.util.http.Skin;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Disguise implements IDisguise {

    private final UUID uuid;
    private String displayName;
    private String skinName;
    private UUID disguiseRank;

    @Override
    public IRank getDisguiseRank() {
        return HolidayAPI.getUnsafeAPI().getRank(disguiseRank);
    }

    @Override
    public void setDisguiseRank(IRank disguiseRank) {
        this.disguiseRank = disguiseRank.getUuid();
    }

    public Skin getSkin() {
        return Skin.getSkinByName(skinName).join();
    }
}
