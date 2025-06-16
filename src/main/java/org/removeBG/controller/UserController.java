package org.removeBG.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.removeBG.dto.UserRequest;
import org.removeBG.dto.UserResponse;
import org.removeBG.response.RemoveBgResponse;
import org.removeBG.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<RemoveBgResponse> createOrUpdateUser(@RequestBody UserRequest userRequest, Authentication authentication) {

        // Validate authentication
        if (authentication == null) {
            log.warn("No authentication found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RemoveBgResponse.builder()
                            .success(false)
                            .data("UNAUTHORIZED: No authentication provided")
                            .build());
        }

        // Check if the authenticated user matches the request
        String authenticatedClerkId = authentication.getName();
        if (!authenticatedClerkId.equals(userRequest.getClerkId())) {
            log.warn("Clerk ID mismatch. Expected: {}, Got: {}", authenticatedClerkId, userRequest.getClerkId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(RemoveBgResponse.builder()
                            .success(false)
                            .data("FORBIDDEN: Unauthorized access - Clerk ID mismatch")
                            .build());
        }

        try {
            log.info("Saving user to database...");
            UserResponse user = userService.saveUser(userRequest);
            log.info("User saved successfully: {}", user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RemoveBgResponse.builder()
                            .success(true)
                            .data(user)
                            .build());

        } catch (Exception e) {
            log.error("Exception while syncing user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RemoveBgResponse.builder()
                            .success(false)
                            .data("Error: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/credits")
    public ResponseEntity<RemoveBgResponse> getUserCredits(Authentication authentication) {
        RemoveBgResponse response;

        try {
            // Validate authentication
            if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
                response = RemoveBgResponse.builder()
                        .success(false)
                        .data("FORBIDDEN: Invalid authentication")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            String clerkId = authentication.getName();
            UserResponse existingUser = userService.getUserByClerkId(clerkId);

            if (existingUser == null) {
                response = RemoveBgResponse.builder()
                        .success(false)
                        .data("User not found")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Integer> creditsMap = new HashMap<>();
            creditsMap.put("credits", existingUser.getCredits());

            response = RemoveBgResponse.builder()
                    .success(true)
                    .data(creditsMap)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Exception while retrieving user credits: {}", e.getMessage(), e);
            response = RemoveBgResponse.builder()
                    .success(false)
                    .data("Server error: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}