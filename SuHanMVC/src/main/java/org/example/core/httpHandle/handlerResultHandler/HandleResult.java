package org.example.core.httpHandle.handlerResultHandler;

import com.sun.net.httpserver.HttpExchange;
import org.example.core.httpHandle.ModelAndView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HandleResult {
    List<HandlerResultHandler> resultHandlerList = new ArrayList<>();

    public HandleResult() {
        resultHandlerList.add(new ResponseBodyHandle());
        resultHandlerList.add(new ModelAndViewHandle());
    }

    public void handleResult(HttpExchange exchange, Object invoked, Object controller, Method method) {
        int index = -1;
        for (int i = 0; i < resultHandlerList.size(); i++) {
            if (resultHandlerList.get(i) instanceof ModelAndView && i != resultHandlerList.size() - 1) {
                index = i;
                continue;
            }
            if (resultHandlerList.get(i).isMatch(controller, method)) {
                resultHandlerList.get(i).handle(exchange, invoked);
                break;
            }
        }
        if (index != -1) {

        }

    }
}
