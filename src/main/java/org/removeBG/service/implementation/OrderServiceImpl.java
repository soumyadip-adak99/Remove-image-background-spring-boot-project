package org.removeBG.service.implementation;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.removeBG.entity.OrderEntity;
import org.removeBG.repository.OrderRepository;
import org.removeBG.service.OrderService;
import org.removeBG.service.RazorPayService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RazorPayService razorPayService;
    private final OrderRepository orderRepository;

    public static final Map<String, PlanDetails> PLAN_DETAILS = Map.of(
            "Basic", new PlanDetails("Basic", 100, 499.00),
            "Premium", new PlanDetails("Premium", 250, 899.00),
            "Ultimate", new PlanDetails("Ultimate", 1000, 1499.00)
    );

    private record PlanDetails(String name, int credits, double amount) {
    }

    @Override
    public Order createOrder(String planId, String clerkId) throws RazorpayException {
        // Validate inputs
        if (clerkId == null || clerkId.isEmpty()) {
            throw new IllegalArgumentException("Clerk ID cannot be empty");
        }

        PlanDetails details = PLAN_DETAILS.get(planId);
        if (details == null) {
            throw new IllegalArgumentException(
                    String.format("Invalid plan id: %s. Valid options are: %s",
                            planId,
                            String.join(", ", PLAN_DETAILS.keySet()))
            );
        }

        try {
            Order razorPayOrder = razorPayService.createOrder(details.amount(), "INR");

            OrderEntity newOrder = OrderEntity.builder()
                    .clerkId(clerkId)
                    .plan(details.name())
                    .credits(details.credits())
                    .amount(details.amount())
                    .orderId(razorPayOrder.get("id"))
                    .payment(false)
                    .build();

            orderRepository.save(newOrder);
            return razorPayOrder;
        } catch (RazorpayException e) {
            log.error("Error while creating order for plan {}: {}", planId, e.getMessage());
            throw new RazorpayException("Failed to create order: " + e.getMessage());
        }
    }
}