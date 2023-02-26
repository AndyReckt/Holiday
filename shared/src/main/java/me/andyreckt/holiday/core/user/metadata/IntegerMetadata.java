package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class IntegerMetadata implements IMetadata<Integer> {

    private final String id;
    private int value;
    private String displayName;

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

}
