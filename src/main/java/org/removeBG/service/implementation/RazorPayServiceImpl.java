package org.removeBG.service.implementation;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.removeBG.dto.UserRequest;
import org.removeBG.dto.UserResponse;
import org.removeBG.entity.OrderEntity;
import org.removeBG.repository.OrderRepository;
import org.removeBG.service.RazorPayService;
import org.removeBG.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorPayServiceImpl implements RazorPayService {

    @Value("${razorpay.key}")
    private String RAZORPAY_KEY;

    @Value("${razorpay.secret}")
    private String RAZORPAY_SECRET_KEY;

    private final OrderRepository orderRepository;
    private final UserService userService;

    @Override
    public Order createOrder(Double amount, String currency) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET_KEY);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1); // Auto-capture payment

            return razorpayClient.orders.create(orderRequest);
        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new RazorpayException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) throws RazorpayException {
        Map<String, Object> result = new HashMap<>();

        try {
            RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET_KEY);

            // Verify payment signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            boolean isValidSignature = Utils.verifyPaymentSignature(attributes, RAZORPAY_SECRET_KEY);

            if (!isValidSignature) {
                log.error("Invalid signature for order: {}", orderId);
                result.put("success", false);
                result.put("message", "Invalid payment signature");
                return result;
            }

            OrderEntity existingOrder = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RazorpayException("Order not found: " + orderId));

            if (Boolean.TRUE.equals(existingOrder.getPayment())) {
                log.warn("Payment already processed for order: {}", orderId);
                result.put("success", false);
                result.put("message", "Payment already processed");
                return result;
            }

            // Update order with payment details
            existingOrder.setPayment(true);
            existingOrder.setRazorpayPaymentId(paymentId);
            existingOrder.setRazorpaySignature(signature);
            orderRepository.save(existingOrder);

            // Add credits to user
            UserResponse userResponse = userService.getUserByClerkId(existingOrder.getClerkId());
            UserRequest userRequest = convertToRequest(userResponse, existingOrder.getCredits());
            userService.saveUser(userRequest);

            result.put("success", true);
            result.put("message", "Payment verified and credits added successfully");
            return result;

        } catch (Exception e) {
            log.error("Payment verification failed for order {}: {}", orderId, e.getMessage());
            throw new RazorpayException("Payment verification failed: " + e.getMessage());
        }
    }

    private UserRequest convertToRequest(UserResponse userResponse, Integer credits) {
        return UserRequest.builder()
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .clerkId(userResponse.getClerkId())
                .credits(userResponse.getCredits() + credits)
                .email(userResponse.getEmail())
                .build();
    }
}