package io.github.zowpy.menu;

import org.bukkit.*;

public enum ButtonSound
{
    CLICK(Sound.CLICK), 
    SUCCESS(Sound.SUCCESSFUL_HIT), 
    FAIL(Sound.DIG_GRASS);
    
    private Sound sound;
    
    private ButtonSound(final Sound sound) {
        this.sound = sound;
    }
    
    public Sound getSound() {
        return this.sound;
    }
}
