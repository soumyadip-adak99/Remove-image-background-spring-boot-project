package org.removeBG.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.removeBG.dto.UserRequest;
import org.removeBG.dto.UserResponse;
import org.removeBG.response.RemoveBgResponse;
import org.removeBG.service.RemoveBackgroundService;
import org.removeBG.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final RemoveBackgroundService removeBackgroundService;
    private final UserService userService;

    @PostMapping("/remove-background")
    public ResponseEntity<?> removeBackground(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(RemoveBgResponse.builder().success(false).data("FORBIDDEN: Invalid authentication").build());
            }

            // Fetch the user
            UserResponse user = userService.getUserByClerkId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(RemoveBgResponse.builder().success(false).data("User not found").build());
            }

            // Check credit balance
            if (user.getCredits() <= 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "No credit available");
                map.put("creditBalance", user.getCredits());
                return ResponseEntity.ok(RemoveBgResponse.builder().success(false).data(map).build());
            }

            // Call Python Flask microservice to remove background
            byte[] processedImage = removeBackgroundService.removeBackground(file);

            // Deduct one credit
            user.setCredits(user.getCredits() - 1);
            userService.saveUser(UserRequest.builder()
                    .clerkId(user.getClerkId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .credits(user.getCredits())
                    .email(user.getEmail())
                    .photoUrl(user.getPhotoUrl())
                    .build());

            // Return image as base64 string
            String base64Image = Base64.getEncoder().encodeToString(processedImage);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(base64Image);

        } catch (Exception e) {
            log.error("Background removal failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RemoveBgResponse.builder().success(false).data("Internal server error").build());
        }
    }
}
