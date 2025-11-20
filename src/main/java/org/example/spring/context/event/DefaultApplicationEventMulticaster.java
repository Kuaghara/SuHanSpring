package org.example.spring.context.event;

import org.example.spring.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultApplicationEventMulticaster implements ApplicationEventMulticaster{
    List<ApplicationListener<?>> listeners = new ArrayList<>();

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        listeners.remove(listener);
    }

    @Override
    public void multicastEvent(ApplicationEvent<?> event) {
        for(ApplicationListener<?> listener: listeners){
            if(isSupportedEvent(listener, event)){
                listener.onEvent(event);
            }
        }
    }

    private Boolean isSupportedEvent(ApplicationListener<?> listener, ApplicationEvent<?> event){
        Class<?> listenerClass = listener.getClass();
        Class<?> eventClass = event.getClass();
        Type[] eventTypeOfListener = listenerClass.getGenericInterfaces();

        for(Type type: eventTypeOfListener){
            if(type instanceof ParameterizedType parameterizedType) {
                Class<?> rawType = (Class<?>)parameterizedType.getRawType();
                if (ApplicationListener.class.isAssignableFrom(rawType)) {

                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Stream<Type> types = Arrays.stream(actualTypeArguments);
                    Optional<Type> typeOptional = types
                            .filter(ltype -> ltype.equals(eventClass))
                            .findAny();
                    return typeOptional.isPresent();
                }
            }
        }
        return false;
    }
}
