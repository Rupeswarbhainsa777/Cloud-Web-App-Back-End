package com.code.CloudShare.RestController;

import com.code.CloudShare.dto.ProfileDto;
import com.code.CloudShare.service.ProfileService;
import com.code.CloudShare.service.UserCreditsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class ClerkWebhooksController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(@RequestHeader("svix-id") String svixId,
                                                @RequestHeader("svix-timestamp") String svixTimestamp,
                                                @RequestHeader("svix-signature") String svixSignature,
                                                @RequestBody String payload) {
        try {
            // ✅ Verify signature
            boolean isValid = verifyWebHookSignature(svixId, svixTimestamp, svixSignature, payload);

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);
            String eventType = rootNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Unhandled event type: " + eventType);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Webhook processing failed: " + e.getMessage(), e);
        }
    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();
        profileService.deleteProfile(clerkId);
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();

        JsonNode emailAddresses = data.path("email_addresses");
        if (emailAddresses.isArray() && emailAddresses.size() > 0) {
            String email = emailAddresses.get(0).path("email_address").asText();
            String firstName = data.path("first_name").asText("");
            String lastName = data.path("last_name").asText("");
            String photoUrl = data.path("image_url").asText("");

            ProfileDto updatedProfile = ProfileDto.builder()
                    .clerkId(clerkId)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .photoUrl(photoUrl)
                    .build();

            updatedProfile = profileService.updateProfile(updatedProfile);

            if (updatedProfile == null) {
                // fallback → create if update failed
                handleUserCreated(data);
            }
        }
    }

    private void handleUserCreated(JsonNode data) {
        String clerkId = data.path("id").asText();

        String email = "";
        JsonNode emailAddresses = data.path("email_addresses");
        if (emailAddresses.isArray() && emailAddresses.size() > 0) {
            email = emailAddresses.get(0).path("email_address").asText();
        }

        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String photoUrl = data.path("image_url").asText("");

        ProfileDto newProfile = ProfileDto.builder()
                .clerkId(clerkId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .photoUrl(photoUrl)
                .build();

        profileService.createProfile(newProfile);
        userCreditsService.createInitialCredits(clerkId);
    }

    /**
     * ✅ Signature verification
     * For now, always returns true. Replace with Clerk's SVIX library for production.
     */
    private boolean verifyWebHookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {
        try {
            // Example with Clerk’s official SDK:
            // Webhook webhook = new Webhook(webhookSecret);
            // webhook.verify(payload, Map.of(
            //        "svix-id", svixId,
            //        "svix-timestamp", svixTimestamp,
            //        "svix-signature", svixSignature
            // ));
            return true; // 🔴 Stub for local testing
        } catch (Exception e) {
            return false;
        }
    }
}
