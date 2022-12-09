package me.andyreckt.holiday.bukkit.util.menu;

import java.io.Serializable;

public interface TypeCallback<T> extends Serializable {
    void callback(final T p0);
}
