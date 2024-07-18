package org.example.repository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@Component
public class ReflectUtil {

    @NonNull
    public <T> T getValueOfObjectField(Object object, String fieldName, Supplier<T> orElseGet) {
        try {
            var field = getFieldOfObject(object.getClass(), fieldName);
            if (!field.canAccess(object)) {
                field.trySetAccessible();
            }
            var value = field.get(object);
            if (value == null) {
                return orElseGet.get();
            }
            //noinspection unchecked
            return (T) value;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void setValueToObjectField(Object object, String fieldName, T value) {
        try {
            var field = getFieldOfObject(object.getClass(), fieldName);
            if (!field.canAccess(object)) {
                field.trySetAccessible();
            }
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getFieldOfObject(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        return clazz.getDeclaredField(fieldName);
    }
}