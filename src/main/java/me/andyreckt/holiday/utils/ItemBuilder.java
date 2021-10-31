package me.andyreckt.holiday.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ItemBuilder {

	 ItemStack itemStack;

	public static ItemBuilder copyOf(ItemBuilder builder) {
		return new ItemBuilder(builder.get());
	}

	public static ItemBuilder copyOf(ItemStack item) {
		return new ItemBuilder(item);
	}

	public ItemBuilder(Material material) {
		this.itemStack = new ItemStack(material);
	}

	public ItemBuilder(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public ItemBuilder setAmount(int amount) {
		this.itemStack.setAmount(amount > 64 ? 64 : amount);
		return this;
	}

	public ItemBuilder setName(String name) {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.setDisplayName(CC.translate(name));
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder addLoreLine(String name) {
		ItemMeta meta = this.itemStack.getItemMeta();
		List<String> lore = meta.getLore();

		if(lore == null) {
			lore = new ArrayList<>();
		}

        lore.add(CC.translate(name));
		meta.setLore(lore);

		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		List<String> toSet = new ArrayList<>();
		ItemMeta meta = this.itemStack.getItemMeta();
		lore.forEach(string -> toSet.add(CC.translate(string)));
		meta.setLore(toSet);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setDurability(int durability) {
		this.itemStack.setDurability((short) durability);
		return this;
	}

	public ItemBuilder setData(int data) {
		this.itemStack.setData(new MaterialData(this.itemStack.getType(), (byte) data));
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
		this.itemStack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder addEnchantment(Enchantment enchantment) {
		this.itemStack.addUnsafeEnchantment(enchantment, 1);
		return this;
	}

	public ItemBuilder setType(Material material) {
		this.itemStack.setType(material);
		return this;
	}

	public ItemBuilder clearLore() {
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.setLore(new ArrayList<>());
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder clearEnchantments() {
		this.itemStack.getEnchantments().keySet().forEach(e -> this.itemStack.removeEnchantment(e));
		return this;
	}

	public ItemBuilder setColor(org.bukkit.Color color) {
		if(this.itemStack.getType() == Material.LEATHER_BOOTS
				|| this.itemStack.getType() == Material.LEATHER_CHESTPLATE
				|| this.itemStack.getType() == Material.LEATHER_HELMET
				|| this.itemStack.getType() == Material.LEATHER_LEGGINGS) {
			LeatherArmorMeta meta = (LeatherArmorMeta) this.itemStack.getItemMeta();
			meta.setColor(color);
			this.itemStack.setItemMeta(meta);
			return this;
		} else {
			throw new IllegalArgumentException("color() only applicable for leather armor.");
		}
	}

	public ItemBuilder setOwner(String owner) {
		SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
		meta.setOwner(owner);
		this.itemStack.setItemMeta(meta);
		return this;
	}

	public ItemStack get() {
		return this.itemStack;
	}

	public static void rename(ItemStack stack, String name) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(CC.translate(name));
		stack.setItemMeta(meta);
	}

	public static ItemStack reloreItem(ItemStack stack, String... lore) {
		return reloreItem(ReloreType.OVERWRITE, stack, lore);
	}

	public static ItemStack reloreItem(ReloreType type, ItemStack stack, String... lores) {
		ItemMeta meta = stack.getItemMeta();

		List<String> lore = meta.getLore();
		if(lore == null) {
			lore = new LinkedList<>();
		}

		switch (type) {
			case APPEND:
				lore.addAll(Arrays.asList(lores));
				meta.setLore(CC.translate(lore));
				break;
			case PREPEND:
				List<String> nLore = new LinkedList<>(Arrays.asList(lores));
				nLore.addAll(CC.translate(lore));
				meta.setLore(CC.translate(nLore));
				break;
			case OVERWRITE:
				meta.setLore(Arrays.asList(lores));
				break;
		}

		stack.setItemMeta(meta);
		return stack;
	}

	public enum ReloreType {
		OVERWRITE,
		PREPEND,
		APPEND
	}

	public static ItemStack createItem(Material material, String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(CC.translate(name));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material material, String name, int amount) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(CC.translate(name));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material material, String name, int amount, short damage) {
		ItemStack item = new ItemStack(material, amount, damage);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(CC.translate(name));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getBackItem() {
		return new ItemBuilder(Material.ARROW).setName(CC.RED + "Back").get();
	}

	public static ItemStack getGoldenHead() {
		return new ItemBuilder(Material.GOLDEN_APPLE).setDurability(0).setName(CC.GOLD + "Golden Head").setLore(
				Arrays.asList(
						CC.GRAY + "Some say consuming the head of a",
						CC.GRAY + "fallen foe strengthens the blood."
				)).get();
	}
}