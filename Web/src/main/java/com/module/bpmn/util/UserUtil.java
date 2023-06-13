package com.module.bpmn.util;

import com.module.bpmn.model.User;
import com.module.bpmn.service.UserService;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public class UserUtil {

    public static User getUserFromContext(Authentication authentication, UserService userService){
        String currentUser = authentication.getName();
        Optional<User> user = userService.getUserByUsername(currentUser);
        return user.orElseGet(User::new);
    }
}
