package me.andyreckt.holiday.bukkit.util.menu.buttons;

import me.andyreckt.holiday.api.user.Profile;
import me.andyreckt.holiday.bukkit.Holiday;
import me.andyreckt.holiday.bukkit.util.item.ItemBuilder;
import me.andyreckt.holiday.bukkit.util.menu.Button;
import me.andyreckt.holiday.bukkit.util.text.CC;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.metadata.BooleanMetadata;
import me.andyreckt.holiday.core.user.metadata.IntegerMetadata;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ListSelectButtonBuilder {

    private String title = "";
    private List<String> description = Collections.emptyList();
    private List<BooleanMetadata> values = Collections.emptyList();
    private MaterialData materialData = new MaterialData(Material.AIR);
    private String buttonId = "";
    private String permission = null;
    private Consumer<Player> afterAction = null;
    private IntegerMetadata currentIndex = null;

    public ListSelectButtonBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ListSelectButtonBuilder withValues(List<BooleanMetadata> values) {
        this.values = values;
        return this;
    }

    public ListSelectButtonBuilder withValues(BooleanMetadata... values) {
        this.values = Arrays.asList(values);
        return this;
    }

    public ListSelectButtonBuilder withDescription(List<String> description) {
        this.description = description;
        return this;
    }

    public ListSelectButtonBuilder withDescription(String... description) {
        this.description = Arrays.asList(description);
        return this;
    }

    public ListSelectButtonBuilder withMaterial(MaterialData materialData) {
        this.materialData = materialData;
        return this;
    }

    public ListSelectButtonBuilder withMaterial(Material material) {
        this.materialData = new MaterialData(material);
        return this;
    }

    public ListSelectButtonBuilder withMaterial(Material material, byte data) {
        this.materialData = new MaterialData(material, data);
        return this;
    }

    public ListSelectButtonBuilder withMaterial(Material material, int data) {
        this.materialData = new MaterialData(material, (byte) data);
        return this;
    }

    public ListSelectButtonBuilder withPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public ListSelectButtonBuilder withAfterAction(Consumer<Player> afterAction) {
        this.afterAction = afterAction;
        return this;
    }

    public ListSelectButtonBuilder withButtonId(String buttonId) {
        this.buttonId = buttonId;
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

                currentIndex = (IntegerMetadata) profile.getMetadata(buttonId, UserProfile.createMetadata(buttonId, 0));

                for (BooleanMetadata defaultValue : values) {
                    BooleanMetadata value = (BooleanMetadata) profile.getMetadata(defaultValue.getId(), defaultValue);
                    int valueIndex = values.indexOf(defaultValue);

                    if (currentIndex.getValue() == valueIndex)
                        builder.lore(value.getValue()  ? enabledSelected(value.getDisplayName())
                                                       : disabledSelected(value.getDisplayName()));
                    else builder.lore(value.getValue() ? enabled(value.getDisplayName())
                                                       : disabled(value.getDisplayName()));
                }

                return builder.build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
                if (permission != null && !player.hasPermission(permission)) return;

                Profile profile = Holiday.getInstance().getApi().getProfile(player.getUniqueId());
                currentIndex = (IntegerMetadata) profile.getMetadata(buttonId, UserProfile.createMetadata(buttonId, 0));
                int index = currentIndex.getValue();

                if (clickType.isShiftClick()) {
                    BooleanMetadata value = (BooleanMetadata) profile.getMetadata(values.get(index).getId(), values.get(index));
                    value.setValue(!value.getValue());
                    Holiday.getInstance().getApi().saveProfile(profile);
                    if (afterAction != null) afterAction.accept(player);
                    return;
                }

                int amount = clickType.isRightClick() ? -1 : 1;
                index += amount;
                index = index >= values.size() ? 0 : index < 0 ? values.size() - 1 : index;
                currentIndex.setValue(index);
                profile.setMetadata(currentIndex);
                Holiday.getInstance().getApi().saveProfile(profile);
            }
        };
    }



    private String enabled(String s) {
        return CC.B_PRIMARY + CC.LINE + " " + CC.GREEN + s;
    }

    private String disabled(String s) {
        return CC.B_PRIMARY + CC.LINE + " " + CC.GRAY + s;
    }

    private String enabledSelected(String s) {
        return CC.B_PRIMARY + CC.LINE_4 + " " + CC.GREEN + s;
    }

    private String disabledSelected(String s) {
        return CC.B_PRIMARY + CC.LINE_4 + " " + CC.GRAY + s;
    }

    private void check() {
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title cannot be null or empty.");
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("Values cannot be null or empty.");
        if (materialData == null || materialData.equals(new MaterialData(Material.AIR))) throw new IllegalArgumentException("MaterialData cannot be null.");
        if (buttonId == null || buttonId.isEmpty()) throw new IllegalArgumentException("ButtonId cannot be null or empty.");
    }
}
