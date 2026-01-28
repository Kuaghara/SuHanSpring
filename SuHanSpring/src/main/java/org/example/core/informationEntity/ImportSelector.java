package org.example.core.informationEntity;

import java.util.List;

public interface ImportSelector {
    // 选择导入的类
    List<String> selectImports(AnnotationMetadata  annotationMetadata);


}
