package org.removeBG.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.removeBG.dto.RazorPayOrderDTO;
import org.removeBG.response.RemoveBgResponse;
import org.removeBG.service.OrderService;
import org.removeBG.service.RazorPayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RazorPayService razorPayService;

    @PostMapping
    public ResponseEntity<RemoveBgResponse> createOrder(@RequestParam String planId,Authentication authentication) {

        if (authentication == null || authentication.getName() == null || authentication.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RemoveBgResponse.builder().success(false).data("Authentication required").build());
        }

        try {
            Order order = orderService.createOrder(planId, authentication.getName());
            RazorPayOrderDTO razorPayResponse = convertToDTO(order);

            return ResponseEntity.status(HttpStatus.CREATED).body(RemoveBgResponse.builder().success(true).data(razorPayResponse).build());
        } catch (RazorpayException e) {
            log.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RemoveBgResponse.builder()
                            .success(false)
                            .data("Order creation failed: " + e.getMessage())
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RemoveBgResponse.builder()
                            .success(false)
                            .data(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String, Object>> verifyOrder(@RequestBody Map<String, String> request) {
        try {
            String razorPayOrderId = request.get("razorpay_order_id");
            String razorPayPaymentId = request.get("razorpay_payment_id");
            String razorPaySignature = request.get("razorpay_signature");

            if (razorPayOrderId == null || razorPayPaymentId == null || razorPaySignature == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Missing payment details"
                        ));
            }

            Map<String, Object> verificationResponse = razorPayService.verifyPayment(
                    razorPayOrderId,
                    razorPayPaymentId,
                    razorPaySignature
            );

            return ResponseEntity.ok(verificationResponse);
        } catch (RazorpayException e) {
            log.error("Payment verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Payment verification failed: " + e.getMessage()
                    ));
        }
    }

    private RazorPayOrderDTO convertToDTO(Order order) {
        try {
            return RazorPayOrderDTO.builder()
                    .id(getStringValue(order, "id"))
                    .entity(getStringValue(order, "entity"))
                    .amount(getIntegerValue(order, "amount"))
                    .amount_due(getIntegerValue(order, "amount_due"))
                    .amount_paid(getIntegerValue(order, "amount_paid"))
                    .currency(getStringValue(order, "currency"))
                    .status(getStringValue(order, "status"))
                    .receipt(getStringValue(order, "receipt"))
                    .build();
        } catch (Exception e) {
            log.error("Error converting order to DTO: {}", e.getMessage());
            throw new RuntimeException("Failed to convert order to DTO", e);
        }
    }

    private String getStringValue(Order order, String key) {
        Object value = order.get(key);
        return value != null ? value.toString() : null;
    }

    private Integer getIntegerValue(Order order, String key) {
        try {
            Object value = order.get(key);
            return value != null ? Integer.parseInt(value.toString()) : null;
        } catch (NumberFormatException e) {
            log.error("Failed to parse integer value for key {}: {}", key, order.get(key));
            return null;
        }
    }
}