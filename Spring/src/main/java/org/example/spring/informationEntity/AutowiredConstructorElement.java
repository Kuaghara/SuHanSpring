package org.example.spring.informationEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class AutowiredConstructorElement implements AutoElement {
    Constructor constructor;
    Field field;
    boolean required;

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

    public Constructor getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor constructor) {
        this.constructor = constructor;
    }

    public AutowiredConstructorElement clone() {
        AutowiredConstructorElement element = new AutowiredConstructorElement();
        element.setConstructor(constructor);
        element.setRequired(required);
        return element;
    }
}
