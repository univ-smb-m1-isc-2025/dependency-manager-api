package com.info803.dependency_manager_api.infrastructure.utils;

import java.lang.reflect.Field;
import jakarta.persistence.MappedSuperclass;
@MappedSuperclass
public abstract class BddEntity {

    // Methods
    /**
     * Updates the current object from the given object
     * It copies all the fields of the given object to the current object
     * and keeps other fields of the current object
     * @param entity
     */
    public void updateFrom(BddEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }

        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(entity);
                if (newValue != null && !java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                    field.set(this, newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field : " + field.getName(), e);
            }
        }
    }
}
