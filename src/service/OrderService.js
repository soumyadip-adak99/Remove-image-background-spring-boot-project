import axios from "axios";
import toast from "react-hot-toast";

export const placeOrder = async ({ planId, getToken, onSuccess, backendurl }) => {
    try {
        const token = await getToken();
        if (!token) {
            throw new Error("Authentication token not available");
        }

        // Validate planId
        const validPlans = ["Basic", "Premium", "Ultimate"];
        if (!validPlans.includes(planId)) {
            throw new Error(`Invalid plan ID: ${planId}`);
        }

        const response = await axios.post(
            `${backendurl}/api/orders`, {},
            {
                params: {
                    planId: planId
                },
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            }
        );

        if (response.data.success) {
            await initializePayment({
                order: response.data.data,
                getToken,
                onSuccess,
                backendurl
            });
            return true;
        } else {
            throw new Error(response.data.data || "Failed to create order");
        }
    } catch (error) {
        console.error("Order creation error:", error);
        let errorMessage = error.message;

        if (error.response) {
            if (error.response.data && error.response.data.data) {
                errorMessage = error.response.data.data;
            } else if (error.response.status === 400) {
                errorMessage = "Invalid request. Please check your input.";
            } else if (error.response.status === 401) {
                errorMessage = "Please sign in to continue";
            }
        }

        toast.error(errorMessage);
        return false;
    }
};

const initializePayment = async ({ order, getToken, onSuccess, backendurl }) => {
    return new Promise((resolve) => {
        const options = {
            key: import.meta.env.VITE_RAZORPAY_KEY_ID,
            amount: order.amount.toString(),
            currency: order.currency,
            name: "Credit Payment",
            description: `Purchase of ${order.amount / 100} credits`,
            order_id: order.id,
            handler: async (response) => {
                try {
                    const token = await getToken();
                    const verificationResponse = await axios.post(
                        `${backendurl}/api/orders/verify-payment`,
                        {
                            razorpay_order_id: response.razorpay_order_id,
                            razorpay_payment_id: response.razorpay_payment_id,
                            razorpay_signature: response.razorpay_signature
                        },
                        {
                            headers: {
                                Authorization: `Bearer ${token}`,
                            },
                        }
                    );

                    if (verificationResponse.data.success) {
                        toast.success("Payment successful! Credits added to your account");
                        onSuccess?.();
                    } else {
                        toast.error(verificationResponse.data.message || "Payment verification failed");
                    }
                } catch (error) {
                    console.error("Payment verification error:", error);
                    toast.error(error?.response?.data?.message || "Payment processing failed");
                } finally {
                    resolve();
                }
            },
            theme: {
                color: "#7C3AED",
            },
            modal: {
                ondismiss: () => {
                    toast.error("Payment window closed");
                    resolve();
                },
            },
        };

        const rzp = new window.Razorpay(options);
        rzp.open();
    });
};