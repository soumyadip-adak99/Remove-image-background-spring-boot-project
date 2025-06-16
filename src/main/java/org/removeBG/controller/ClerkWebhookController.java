package org.removeBG.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.removeBG.dto.UserRequest;
import org.removeBG.response.RemoveBgResponse;
import org.removeBG.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class ClerkWebhookController {

    private final UserService userService;

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/clerk")
    public ResponseEntity<RemoveBgResponse> createWebhook(@RequestHeader("svix-id") String svixId,
                                           @RequestHeader("svix-timestamp") String svixTimestamp,
                                           @RequestHeader("svix-signature") String svixSignature,
                                           @RequestBody String payload) {

        RemoveBgResponse response;

        try {
            boolean isValid = verifiyWebhookSignature(svixId, svixTimestamp, svixSignature, payload);
            if (!isValid) {
                response = RemoveBgResponse.builder().data("Invalid signature").success(false).build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);
            String eventType = jsonNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated(jsonNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdate(jsonNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDelete(jsonNode.path("data"));
                    break;
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage());
            response = RemoveBgResponse.builder().data(e.getMessage()).success(false).build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUserCreated(JsonNode data) {
        UserRequest request = parseUserData(data);
        userService.saveUser(request);
    }

    private void handleUserUpdate(JsonNode data) {
        UserRequest request = parseUserData(data);
        userService.saveUser(request);
    }

    private void handleUserDelete(JsonNode data) {
        String clerkId = data.path("id").asText();
        userService.deleteUserByClerkId(clerkId);
    }

    private UserRequest parseUserData(JsonNode data) {
        return UserRequest.builder()
                .clerkId(data.path("id").asText())
                .email(data.path("email_addresses").get(0).path("email_address").asText())
                .firstName(data.path("first_name").asText())
                .lastName(data.path("last_name").asText())
                .photoUrl(data.path("image_url").asText())
                .build();
    }

    private boolean verifiyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {
        // TODO: Use Clerk SDK to verify the Svix signature using webhookSecret
        return true; // for testing
    }
}
