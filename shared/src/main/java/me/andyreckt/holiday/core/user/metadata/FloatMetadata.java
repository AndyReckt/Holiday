package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class FloatMetadata implements IMetadata<Float> {

    private final String id;
    private float value;
    private String displayName;

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void setValue(Float value) {
        this.value = value;
    }

}
