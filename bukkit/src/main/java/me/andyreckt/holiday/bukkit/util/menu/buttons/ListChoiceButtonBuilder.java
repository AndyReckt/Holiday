package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.api.user.IMetadata;
import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.text.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "rawtypes"})
public class ListChoiceButtonBuilder {

    private String title = "";
    private List<String> description = Collections.emptyList();
    private List<IMetadata> values = Collections.emptyList();
    private String metadataId = "";
    private IMetadata defaultValue = null;
    private MaterialData materialData = new MaterialData(Material.AIR);
    private String permission = null;
    private Consumer<Player> afterAction = null;


    public ListChoiceButtonBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ListChoiceButtonBuilder withMetadata(String metadataId) {
        this.metadataId = metadataId;
        return this;
    }

    public ListChoiceButtonBuilder withValues(List<IMetadata> values) {
        this.values = values;
        return this;
    }

    public ListChoiceButtonBuilder withValues(IMetadata... values) {
        this.values = Arrays.asList(values);
        return this;
    }

    public ListChoiceButtonBuilder withDescription(List<String> description) {
        this.description = description;
        return this;
    }

    public ListChoiceButtonBuilder withDescription(String... description) {
        this.description = Arrays.asList(description);
        return this;
    }

    public ListChoiceButtonBuilder withMaterial(MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }

    public ListChoiceButtonBuilder withMaterial(Material material) {
        this.materialData = new MaterialData(material);
        return this;
    }

    public ListChoiceButtonBuilder withMaterial(Material material, byte data) {
        this.materialData = new MaterialData(material, data);
        return this;
    }

    public ListChoiceButtonBuilder withMaterial(Material material, int data) {
        this.materialData = new MaterialData(material, (byte) data);
        return this;
    }

    public ListChoiceButtonBuilder withDefaultValue(IMetadata defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ListChoiceButtonBuilder withPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public ListChoiceButtonBuilder withAfterAction(Consumer<Player> afterAction) {
        this.afterAction = afterAction;
        return this;
    }

    public Button build() {
        this.check();
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
                ItemBuilder builder = new ItemBuilder(materialData.toItemStack());
                builder.displayname(CC.PRIMARY + title);
                if (!description.isEmpty()) description.forEach(s -> builder.lore(CC.GRAY + s));
                else builder.lore(CC.GRAY + "???");
                builder.lore("");

                IMetadata playerCurrentMetadata = profile.getMetadata(metadataId, defaultValue);

                for (IMetadata value : values) {
                    if (value.getValue().equals(playerCurrentMetadata.getValue())) {
                        builder.lore(selected(value.getDisplayName()));
                    } else {
                        builder.lore(unselected(value.getDisplayName()));
                    }
                }

                return builder.build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (permission != null && !player.hasPermission(permission)) return;

                Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
                IMetadata playerCurrentMetadata = profile.getMetadata(metadataId, defaultValue);

                int amount = clickType.isRightClick() ? -1 : 1;
                int index = values.indexOf(playerCurrentMetadata) + amount;
                IMetadata newVal = index >= values.size() ? defaultValue : index < 0 ? values.get(values.size() - 1) : values.get(index);

                profile.setMetadata(newVal);
                Holiday.getInstance().getApi().saveProfile(profile);

                if (afterAction != null) afterAction.accept(player);
            }
        };
    }

    private String unselected(String s) {
        return CC.B_PRIMARY + CC.LINE + " " + CC.GRAY + s;
    }

    private String selected(String s) {
        return CC.B_PRIMARY + CC.LINE + " " + CC.GREEN + s;
    }

    private void check() {
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title cannot be null or empty.");
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("Values cannot be null or empty.");
        if (metadataId == null || metadataId.isEmpty()) throw new IllegalArgumentException("MetadataId cannot be null or empty.");
        if (defaultValue == null) throw new IllegalArgumentException("DefaultValue cannot be null.");
        if (materialData == null || materialData.equals(new MaterialData(Material.AIR))) throw new IllegalArgumentException("MaterialData cannot be null.");
        String id = defaultValue.getId();
        values.forEach(value -> {
            if (value == null) throw new IllegalArgumentException("Value cannot be null.");
            if (!value.getId().equals(id)) throw new IllegalArgumentException("Value id must be the same as the default value id.");
        });
    }
}
