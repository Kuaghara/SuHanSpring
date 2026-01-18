package org.example.core.httpHandle;

import java.util.Locale;
import java.util.Objects;

public class RouteKey {
    private final String requestMethod;
    private final String requestPath;

    public RouteKey(String requestMethod, String requestPath) {
        this.requestMethod = normalizeMethod(requestMethod);
        this.requestPath = normalizePath(requestPath);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    private static String normalizeMethod(String method) {
        if (method == null) return "";
        return method.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) return "/";
        String trimmed = path.trim();
        if (!trimmed.startsWith("/")) trimmed = "/" + trimmed;
        while (trimmed.contains("//")) trimmed = trimmed.replace("//", "/");
        if (trimmed.length() > 1 && trimmed.endsWith("/")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        return trimmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteKey routeKey = (RouteKey) o;
        return Objects.equals(requestMethod, routeKey.requestMethod)
                && Objects.equals(requestPath, routeKey.requestPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestMethod, requestPath);
    }

    @Override
    public String toString() {
        return requestMethod + " " + requestPath;
    }


}
