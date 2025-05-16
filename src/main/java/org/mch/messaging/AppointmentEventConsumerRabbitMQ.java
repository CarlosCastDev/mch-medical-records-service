package org.mch.messaging;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.mch.dto.AppointmentEvent;
import org.mch.entity.MedicalRecord;
import org.mch.qualifiers.RabbitProducer;

@RabbitProducer
@ApplicationScoped
public class AppointmentEventConsumerRabbitMQ implements AppointmentEventConsumer {

    private static final Logger LOGGER = Logger.getLogger(AppointmentEventConsumerRabbitMQ.class);

    @Incoming("appointment-events-in")
    @Blocking
    @Transactional
    @Override
    public void processAppointmentEvent(JsonObject json) {
        AppointmentEvent event = json.mapTo(AppointmentEvent.class);
        LOGGER.infof("Evento recibido: %s", event);

        // Lógica para vincular la cita con el expediente médico
        // Ejemplo de pseudo-código:
        // MedicalRecord record = medicalRecordService.findByPatientId(event.getPatientId());
        MedicalRecord record = MedicalRecord.findByPatientId(event.getPatientId());
        if (record == null) {
            record = new MedicalRecord();
            record.patientId =event.getPatientId();
            record.persist();
            LOGGER.infof("Se creo el expediente: %s", event);
        }
    }
}
