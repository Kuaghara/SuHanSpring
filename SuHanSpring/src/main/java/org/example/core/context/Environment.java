package org.example.core.context;


///  抄过来的，OnPropertyCondition要用就抄过来了
@FunctionalInterface
public interface Environment {

    Environment SYSTEM = System::getenv;

    String get(String key);
}
