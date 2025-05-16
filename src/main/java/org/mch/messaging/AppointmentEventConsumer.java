package org.mch.messaging;

import io.vertx.core.json.JsonObject;

public interface AppointmentEventConsumer {
    void processAppointmentEvent(JsonObject json);
}