package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class TMetadata<T> implements IMetadata<T> {

    private final String id;
    private T value;
    private String displayName;

    @Override
    public Class<T> getType() {
        return (Class<T>) value.getClass();
    }

}

