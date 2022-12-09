package me.andyreckt.holiday.core.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {
    
     JsonObject json = new JsonObject();

    public JsonBuilder append(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder append(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder append(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder append(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonBuilder append(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public JsonObject get() {
        return this.json;
    }
}
