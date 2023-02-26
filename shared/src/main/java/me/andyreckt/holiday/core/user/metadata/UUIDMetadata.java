package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class UUIDMetadata implements IMetadata<UUID> {

    private final String id;
    private UUID value;
    private String displayName;

    @Override
    public Class<UUID> getType() {
        return UUID.class;
    }
}
