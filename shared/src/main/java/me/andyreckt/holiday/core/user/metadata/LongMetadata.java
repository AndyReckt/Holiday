package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class LongMetadata implements IMetadata<Long> {

    private final String id;
    private long value;
    private String displayName;

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public void setValue(Long value) {
        this.value = value;
    }

}
