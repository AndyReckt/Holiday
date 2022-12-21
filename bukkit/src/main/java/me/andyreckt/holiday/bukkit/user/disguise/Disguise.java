package me.andyreckt.holiday.bukkit.user.disguise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IDisguise;
import me.andyreckt.holiday.api.user.IRank;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Disguise implements IDisguise {

    private final UUID uuid;
    private String displayName;
    private String skinName;
    private IRank disguiseRank;

}
