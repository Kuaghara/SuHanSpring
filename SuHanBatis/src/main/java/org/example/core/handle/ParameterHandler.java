package org.example.core.handle;

import org.example.core.annotations.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class ParameterHandler {

    //判断传入的参数类型是否和sql语句中的参数类型一致
    // parameterType sql语句中的参数类型
    // t 传入的参数
    //上面的if判断是用来进行自动判断的，一般都是一个数字或者一个字符串吧（
    public <T> boolean isTypeMatch(String parameterType, T t) {
        if (parameterType == null) {
            return String.class.isAssignableFrom(t.getClass()) ||
                    Integer.class.isAssignableFrom(t.getClass())||
                    Long.class.isAssignableFrom(t.getClass());
        }
        else if (t != null && parameterType.isEmpty()) {
            //插入和修改的sql语句
           return true;
        }
        else {
            Class<?> clazz = resolvePrimitiveType(parameterType);
            return clazz.isAssignableFrom(t.getClass());
        }
    }

    private Class<?> resolvePrimitiveType(String parameterType) {
        return switch (parameterType) {
            case "_int" -> Integer.class;
            case "_long" -> Long.class;
            case "_float" -> Float.class;
            case "_double" -> Double.class;
            case "_boolean" -> Boolean.class;
            case "_char" -> Character.class;
            case "_byte" -> Byte.class;
            case "_short" -> Short.class;
            case "_void" -> Void.class;
            case "map" -> Map.class;
            default -> otherType(parameterType);
        };
    }

    private Class<?> otherType(String parameterType) {
        try {
            return  Class.forName(parameterType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e+"查找类型时错误");
        }
    }

    //新写一个方法，用于拆分@Parma注解变成map传入
    public Map<String , Object> readTheShapeParameter(Method method ,Object[] args){
        Map<String , Object> map = new HashMap<>();
        Parameter [] parameters = method.getParameters();
        int i = 0;
        for(Parameter parameter : parameters){
            Annotation annotation = parameter.getAnnotation(Param.class);
            if(annotation != null){
                String key = ((Param) annotation).value();
                map.put(key, args[i++]);
            }
        }
        return map;
    }
}
