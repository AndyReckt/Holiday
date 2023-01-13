package me.andyreckt.holiday.api.user;

import java.util.UUID;

public interface IDisguise {

    String getDisplayName();
    String getSkinName();
    IRank getDisguiseRank();
    UUID getUuid();

    void setDisplayName(String displayName);
    void setSkinName(String skinName);
    void setDisguiseRank(IRank disguiseRank);

}
