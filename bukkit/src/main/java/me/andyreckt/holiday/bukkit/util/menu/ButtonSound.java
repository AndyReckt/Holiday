package me.andyreckt.holiday.bukkit.util.menu;

import org.bukkit.Sound;

public enum ButtonSound {
    CLICK(Sound.CLICK),
    SUCCESS(Sound.SUCCESSFUL_HIT),
    FAIL(Sound.DIG_GRASS);

    private final Sound sound;

    ButtonSound(final Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return this.sound;
    }
}
