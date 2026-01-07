package org.example.core.handle;

public class HandlerManager {
    private static final HandlerManager handlerManager = new HandlerManager();
    private static ObjectHandler objectHandler = new ObjectHandler();
    private static ParameterHandler parameterHandler = new ParameterHandler();
    private static StatementHandler statementHandler = new StatementHandler();
    private static MapperHandler  mapperHandler = new MapperHandler();

    private HandlerManager(){}

    public static HandlerManager getHandlerManager(){
        return handlerManager;
    }

    public ObjectHandler getObjectHandler() {
        return objectHandler;
    }

    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    public StatementHandler getStatementHandler() {
        return statementHandler;
    }

     public MapperHandler getMapperHandler() {
        return mapperHandler;
    }
}
