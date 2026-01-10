package org.example.mapper;

import org.example.core.annotations.Param;
import org.example.entity.User;

import java.util.List;

public interface TestMapper {
    List<User> selectAllUser();

    User getUserById(@Param("uid") int i);

    int addUser(User user);

    int insertUser(@Param("id") int id, @Param("username") String name, @Param("password") String password);

    User testMap();
}