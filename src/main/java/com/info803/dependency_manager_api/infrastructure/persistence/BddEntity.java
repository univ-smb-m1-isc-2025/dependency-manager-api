package com.info803.dependency_manager_api.infrastructure.persistence;

import java.lang.reflect.Field;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
abstract class BddEntity {

    // Attributes
    @Id
    @GeneratedValue
    protected Long id;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
