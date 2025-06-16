package org.removeBG.service.implementation;

import lombok.RequiredArgsConstructor;
import org.removeBG.dto.UserRequest;
import org.removeBG.dto.UserResponse;
import org.removeBG.entity.UserEntity;
import org.removeBG.repository.UserRepository;
import org.removeBG.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse saveUser(UserRequest userRequest) {
        Optional<UserEntity> optionalUser = userRepository.findByClerkId(userRequest.getClerkId());

        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setFirstName(userRequest.getFirstName());
            existingUser.setLastName(userRequest.getLastName());
            existingUser.setPhotoUrl(userRequest.getPhotoUrl());

            if (userRequest.getCredits() != null) {
                existingUser.setCredits(userRequest.getCredits());
            }

            existingUser = userRepository.save(existingUser);
            return mapToResponse(existingUser);
        }

        UserEntity newUser = mapToEntity(userRequest);
        userRepository.save(newUser);
        return mapToResponse(newUser);
    }

    @Override
    public UserResponse getUserByClerkId(String clerkId) {
        UserEntity user = userRepository.findByClerkId(clerkId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public void deleteUserByClerkId(String clerkId) {
        UserEntity user = userRepository.findByClerkId(clerkId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);
    }

    private UserEntity mapToEntity(UserRequest userRequest) {
        return UserEntity.builder()
                .clerkId(userRequest.getClerkId())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .photoUrl(userRequest.getPhotoUrl())
                .build();
    }

    private UserResponse mapToResponse(UserEntity newUser) {
        return UserResponse.builder()
                .clerkId(newUser.getClerkId())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .photoUrl(newUser.getPhotoUrl())
                .credits(newUser.getCredits())
                .build();
    }
}
