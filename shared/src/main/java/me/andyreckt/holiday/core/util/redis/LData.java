package me.andyreckt.holiday.core.util.redis;

import lombok.Data;
import java.lang.reflect.Method;

@Data
public class LData {

    private final Object object;
    private final Method method;
    private final Class<?> clazz;

}
