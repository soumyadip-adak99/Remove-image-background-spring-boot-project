package org.removeBG.service;

import org.removeBG.dto.UserRequest;
import org.removeBG.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse saveUser(UserRequest userRequest);

    UserResponse getUserByClerkId(String clerkId);

    void deleteUserByClerkId(String clerkId);
}
