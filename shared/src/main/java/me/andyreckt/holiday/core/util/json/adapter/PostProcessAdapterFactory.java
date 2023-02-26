package me.andyreckt.holiday.core.util.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.andyreckt.holiday.core.user.UserProfile;
import me.andyreckt.holiday.core.user.punishment.Punishment;

import java.io.IOException;

public class PostProcessAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter jsonWriter, T t) throws IOException {
                delegate.write(jsonWriter, t);
            }

            @Override
            public T read(JsonReader jsonReader) throws IOException {
                T obj = delegate.read(jsonReader);

                if (obj instanceof Punishment) {
                    ((Punishment) obj).postProcess();
                } else if (obj instanceof UserProfile) {
                    ((UserProfile) obj).postProcess();
                }
                return obj;
            }
        };
    }
}