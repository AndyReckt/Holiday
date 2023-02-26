package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

@Getter @Setter
@AllArgsConstructor
public class StringMetadata implements IMetadata<String> {

    private final String id;
    private String value;
    private String displayName;

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
