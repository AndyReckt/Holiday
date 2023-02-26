package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class ByteMetadata implements IMetadata<Byte> {

    private final String id;
    private byte value;
    private String displayName;

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public void setValue(Byte value) {
        this.value = value;
    }

}
