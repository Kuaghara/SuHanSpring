package org.example.core.httpHandle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class favicon implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            // 返回一个简单的 16x16 像素的透明图标
            String favicon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAZdEVYdFNvZnR3YXJlAHF1aWNrIHZlcnNpb24gMS4wLjAgV2luMzIAAAAAAElFTkSuQmCC";
            byte[] bytes = favicon.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "image/x-icon");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
