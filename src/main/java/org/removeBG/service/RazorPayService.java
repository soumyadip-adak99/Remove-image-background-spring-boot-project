package org.removeBG.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

import java.util.Map;

public interface RazorPayService {
    Order createOrder(Double amount, String currency) throws RazorpayException;

    Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) throws RazorpayException;
}
