package me.andyreckt.holiday.core.user.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.andyreckt.holiday.api.user.IMetadata;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class ListMetadata implements IMetadata<List> {

    private String id;
    private List value;
    private String displayName;

    @Override
    public Class<List> getType() {
        return List.class;
    }
}
