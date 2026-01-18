package org.example.core.httpHandle.argsHandle;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.httpHandle.handlerResultHandler.HandlerResultHandler;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class AnnotationHandle {
    List<HandleArgs> handlerResultHandlerHandles = new ArrayList<>();

    public AnnotationHandle() {
        handlerResultHandlerHandles.add(new RequestParamHandle());
        handlerResultHandlerHandles.add(new RequestHeaderHandle());
        handlerResultHandlerHandles.add(new RequestBodyHandle());
    }

    public void addArgsAnnotationHandle(HandleArgs handleArgs){
        handlerResultHandlerHandles.add(handleArgs);
    }

    public Object handleParameter(HttpExchange exchange , Parameter parameter){
        for (HandleArgs handleArgs : handlerResultHandlerHandles) {
            if(handleArgs.support(exchange, parameter)){
                return handleArgs.handle(exchange,parameter);
            }
        }
        return null;
    }
}
