package me.andyreckt.holiday.api.user;

public interface IMetadata<T> {

    String getId();

    String getDisplayName();
    void setDisplayName(String displayName);

    Class<T> getType();

    T getValue();
    void setValue(T value);

}
