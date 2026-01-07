package org.example.spring.informationEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AutowiredMethodElement implements AutoElement {
    Method method;
    Field field;
    boolean required;

    @Override
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
