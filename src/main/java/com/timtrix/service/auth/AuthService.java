package com.timtrix.service.auth;


import com.timtrix.dtos.UserDTO;

import java.util.Map;

public interface AuthService {
    Map<String, Object> createUser(UserDTO userDTO);
}
