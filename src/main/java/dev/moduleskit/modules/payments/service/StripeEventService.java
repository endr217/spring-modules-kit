package dev.moduleskit.modules.payments.service;

import com.stripe.model.Event;

public interface StripeEventService {

    /**
     * 
     * @param event
     */
    public void handleEvent(Event event);
}
