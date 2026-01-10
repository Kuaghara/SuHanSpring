package org.example.core.informationEntity;

public interface ClassMetadata {

    String getClassName();

    boolean isInterface();

    boolean isAnnotation();

    boolean isAbstract();
    default boolean isConcrete() {
        return !(isInterface() || isAbstract());
    }

    boolean isFinal();

    boolean isIndependent();



}