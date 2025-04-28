package com.github.zigcat.greenhub.payment_provider.presentation.controllers;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {
    private static final String STRIPE_SECRET = "your_stripe_webhook_secret";

    @PostMapping("/webhook/stripe")
    public void handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, STRIPE_SECRET);
            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getData().getObject();
                String subscriptionId = session.getSubscription();
                System.out.println("Subscription ID: " + subscriptionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
