package org.mch.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiService;
import com.google.api.core.ApiService.Listener;

import com.google.api.gax.core.FixedExecutorProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import io.quarkus.runtime.Startup;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mch.dto.AppointmentEvent;
import org.mch.entity.MedicalRecord;
import org.mch.qualifiers.GCloudProducer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Startup
@ApplicationScoped
@Slf4j
public class AppointmentEventConsumerGGLCloud implements AppointmentEventConsumer {

    @ConfigProperty(name = "pubsub.project-id")
    String PROJECT_ID;
    @ConfigProperty(name = "pubsub.subscription-id")
    String SUBSCRIPTION_ID;

    private Subscriber subscriber;
    private final ObjectMapper objectMapper = new ObjectMapper();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    FixedExecutorProvider executorProvider = FixedExecutorProvider.create(executor);

    @PostConstruct
    void init() {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID);

        subscriber = Subscriber.newBuilder(subscriptionName, (MessageReceiver) (message, consumer) -> {
                    try {
                        String jsonString = message.getData().toStringUtf8();
                        JsonObject json = new JsonObject(jsonString);

                        processAppointmentEvent(json);

                        // Confirma el mensaje para evitar reenvío
                        consumer.ack();
                    } catch (Exception e) {
                        log.error("❌ Error al procesar mensaje", e);
                        consumer.nack(); // Se reintentará
                    }
                }).setExecutorProvider(executorProvider)
                .build();

        subscriber.addListener(new Listener() {
            @Override
            public void failed(ApiService.State from, Throwable failure) {
                log.error("⛔ Error en el Subscriber: ", failure);
            }
        }, executor);

        subscriber.startAsync().awaitRunning();
        log.info("✅ Suscripción iniciada a '{}'", SUBSCRIPTION_ID);
    }

    @PreDestroy
    void shutdown() {
        if (subscriber != null) {
            subscriber.stopAsync();
        }
        executor.shutdown();
        log.info("🛑 Subscriber detenido");
    }

    @Transactional
    @Override
    public void processAppointmentEvent(JsonObject json) {
        AppointmentEvent event = json.mapTo(AppointmentEvent.class);
        log.info("Evento recibido: {}", event);

        // Lógica para vincular la cita con el expediente médico
        // Ejemplo de pseudo-código:
        // MedicalRecord record = medicalRecordService.findByPatientId(event.getPatientId());
        MedicalRecord record = MedicalRecord.findByPatientId(event.getPatientId());
        if (record == null) {
            record = new MedicalRecord();
            record.patientId =event.getPatientId();
            record.persist();
            log.info("Se creo el expediente: {}", event);
        }
    }
}
