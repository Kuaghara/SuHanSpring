package org.example.spring.informationEntity;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AutowiredMethodElement {
    Method method;
    Constructor<?> constructor;
    boolean required;
}
