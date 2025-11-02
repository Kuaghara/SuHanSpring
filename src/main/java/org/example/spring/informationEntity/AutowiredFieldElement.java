package org.example.spring.informationEntity;

import java.lang.reflect.Field;

public class AutowiredFieldElement implements AutoElement {
    Field field;
    boolean required = false;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
