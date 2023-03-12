package me.andyreckt.holiday.api.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

public interface IRank extends Comparable<IRank> {

    @SerializedName("_id")
    UUID getUuid();

    String getName();
    void setName(String name);

    String getPrefix();
    void setPrefix(String prefix);

    String getSuffix();
    void setSuffix(String suffix);

    String getDisplayName();
    void setDisplayName(String displayName);

    String getColor();
    void setColor(String color);

    boolean isBold();
    void setBold(boolean bold);

    boolean isItalic();
    void setItalic(boolean italic);

    boolean isDefault();
    void setDefault(boolean isDefault);

    boolean isStaff();
    void setStaff(boolean isStaff);

    boolean isAdmin();
    void setAdmin(boolean isAdmin);

    boolean isOp();
    void setOp(boolean isOP);

    boolean isVisible();
    void setVisible(boolean isVisible);

    int getPriority();
    void setPriority(int priority);

    List<String> getPermissions();
    void addPermission(String permission);
    void removePermission(String permission);

    List<UUID> getChilds();
    void addChild(UUID uuid);
    void removeChild(UUID uuid);

    default boolean isAboveOrEqual(IRank rank) {
        return rank.getPriority() <= getPriority();
    }

}
