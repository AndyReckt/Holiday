package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class DoubleMetadata implements IMetadata<Double> {

    private final String id;
    private double value;
    private String displayName;

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        this.value = value;
    }

}
