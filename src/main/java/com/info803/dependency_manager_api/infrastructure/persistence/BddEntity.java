package com.info803.dependency_manager_api.infrastructure.persistence;

import java.lang.reflect.Field;
import jakarta.persistence.MappedSuperclass;
@MappedSuperclass
public abstract class BddEntity {

    // Methods
    /**
     * Updates the current object from the given entity
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
