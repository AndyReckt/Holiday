package me.andyreckt.holiday.bukkit.util.text;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.*;

public class TextComponentBuilder
{
    private final TextComponent textComponent;
    
    public TextComponentBuilder(final String text) {
        this.textComponent = new TextComponent(text);
    }
    
    public TextComponentBuilder setHoverEvent(final String str) {
        final ComponentBuilder componentBuilder = new ComponentBuilder(CC.translate(str));
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
        return this;
    }
    
    public TextComponentBuilder setClickEvent(final ClickEvent.Action action, final String str) {
        this.textComponent.setClickEvent(new ClickEvent(action, CC.translate(str)));
        return this;
    }
    
    public TextComponentBuilder setBold(final boolean bold) {
        this.textComponent.setBold(bold);
        return this;
    }
    
    public TextComponentBuilder setText(final String text) {
        this.textComponent.setText(CC.translate(text));
        return this;
    }
    
    public TextComponentBuilder setItalic(final boolean italic) {
        this.textComponent.setItalic(italic);
        return this;
    }
    
    public TextComponentBuilder setColor(final ChatColor color) {
        this.textComponent.setColor(color);
        return this;
    }
    
    public TextComponentBuilder setObfuscated(final boolean obfuscated) {
        this.textComponent.setObfuscated(obfuscated);
        return this;
    }
    
    public TextComponentBuilder setUnderlined(final boolean underlined) {
        this.textComponent.setUnderlined(underlined);
        return this;
    }
    
    public TextComponentBuilder setStrikeTrough(final boolean strikethrough) {
        this.textComponent.setStrikethrough(strikethrough);
        return this;
    }
    
    public TextComponent toText() {
        return this.textComponent;
    }
}
