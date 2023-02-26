package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

import java.util.Map;

@Getter @Setter
@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class MapMetadata implements IMetadata<Map> {

    private final String id;
    private Map value;
    private String displayName;

    @Override
    public Class<Map> getType() {
        return Map.class;
    }
}
