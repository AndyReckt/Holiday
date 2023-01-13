package me.andyreckt.holiday.core.util.enums;

import lombok.Getter;

@Getter
public enum ChatChannel {
    GLOBAL("Global"),
    STAFF("Staff"),
    ADMIN("Admin"),
    ;

    private final String name;

    ChatChannel(String name) {
        this.name = name;
    }
}
