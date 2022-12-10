package me.andyreckt.holiday.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

public class ChatComponentBuilder extends ComponentBuilder {

	 static Field partsField;
	 static Field currField;

	static {
		try {
			currField = ComponentBuilder.class.getDeclaredField("current");
			partsField = ComponentBuilder.class.getDeclaredField("parts");

			currField.setAccessible(true);
			partsField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public ChatComponentBuilder(String text) {
		super("");
		this.parse(text);
	}

	public TextComponent getCurrent() {
		try {
			return (TextComponent) currField.get(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setCurrent(TextComponent tc) {
		try {
			currField.set(this, tc);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public List<BaseComponent> getParts() {
		try {
			return (List<BaseComponent>) partsField.get(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public ChatComponentBuilder setCurrentHoverEvent(HoverEvent hoverEvent) {
		this.getCurrent().setHoverEvent(hoverEvent);

		return this;
	}

	public ChatComponentBuilder setCurrentClickEvent(ClickEvent clickEvent) {
		this.getCurrent().setClickEvent(clickEvent);

		return this;
	}

	public ChatComponentBuilder attachToEachPart(HoverEvent hoverEvent) {
		(this.getParts()).forEach(part -> {
			if(part.getHoverEvent() == null) {
				part.setHoverEvent(hoverEvent);
			}
		});

		this.getCurrent().setHoverEvent(hoverEvent);

		return this;
	}

	public ChatComponentBuilder attachToEachPart(ClickEvent clickEvent) {
		(this.getParts()).forEach(part -> {
			if(part.getClickEvent() == null) {
				part.setClickEvent(clickEvent);
			}
		});

		this.getCurrent().setClickEvent(clickEvent);

		return this;
	}

	public ChatComponentBuilder parse(String text) {
		String regex = "[&§]{1}([a-fA-Fl-oL-O0-9-r]){1}";
		text = text.replaceAll(regex, "§$1");

		if(!Pattern.compile(regex).matcher(text).find()) {
			this.append(text);
			return this;
		}

		String[] words = text.split(regex);
		int index = words[0].length();

		for (String word : words) {
			try {
				if(index != words[0].length()) {
					this.append(word);

					final ChatColor color = ChatColor.getByChar(text.charAt(index - 1));

					if(color == ChatColor.BOLD) {
						this.bold(true);
					} else if(color == ChatColor.STRIKETHROUGH) {
						this.strikethrough(true);
					} else if(color == ChatColor.MAGIC) {
						this.obfuscated(true);
					} else if(color == ChatColor.UNDERLINE) {
						this.underlined(true);
					} else if(color == ChatColor.RESET) {
						this.bold(false);
						this.strikethrough(false);
						this.obfuscated(false);
						this.underlined(false);
					} else {
						this.color(color);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			index += word.length() + 2;
		}

		return this;
	}

	public ChatComponentBuilder append(String text, BaseComponent[] hover) {
		this.append(text);

		if(hover != null) {
			this.getCurrent().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
		}

		return this;
	}

	public ChatComponentBuilder append(TextComponent textComponent) {
		if(textComponent == null) {
			return this;
		}

		String text = textComponent.getText();
		ChatColor color = textComponent.getColor();
		boolean underline = textComponent.isUnderlined();
		boolean italic = textComponent.isUnderlined();
		boolean strike = textComponent.isStrikethrough();
		HoverEvent he = textComponent.getHoverEvent();
		ClickEvent ce = textComponent.getClickEvent();

		append(text);
		color(color);
		underlined(underline);
		italic(italic);
		strikethrough(strike);
		event(he);
		event(ce);

		if(textComponent.getExtra() != null) {
			for (BaseComponent bc : textComponent.getExtra()) {
				if(bc instanceof TextComponent) {
					append((TextComponent) bc);
				}
			}
		}

		return this;
	}
}