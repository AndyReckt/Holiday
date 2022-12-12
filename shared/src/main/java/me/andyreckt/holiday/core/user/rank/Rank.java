package me.andyreckt.holiday.core.user.rank;

import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IRank;

import java.util.List;
import java.util.UUID;


@Getter @Setter
public class Rank implements IRank {

    private final UUID uuid;

    private String name;

    private String prefix = "";
    private String suffix = "";
    private String displayName = "";
    private String color = "GREEN";

    private boolean bold = false;
    private boolean italic = false;

    private boolean isDefault = false;

    private boolean staff = false;
    private boolean admin = false;
    private boolean op = false;

    private boolean visible = true;

    private int priority = 0;

    private List<String> permissions;
    private List<UUID> childs;



    public Rank(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    @Override
    public void addPermission(String permission) {
        getPermissions().add(permission);
    }

    @Override
    public void removePermission(String permission) {
        getPermissions().remove(permission);
    }

    @Override
    public void addChild(UUID uuid) {
        getChilds().add(uuid);
    }

    @Override
    public void removeChild(UUID uuid) {
        getChilds().remove(uuid);
    }
}
