package org.mch.messaging;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.mch.dto.AppointmentEvent;
import org.mch.entity.MedicalRecord;

@ApplicationScoped
public class AppointmentEventConsumer {

    private static final Logger LOGGER = Logger.getLogger(AppointmentEventConsumer.class);

    @Incoming("appointment-events-in")
    @Blocking
    @Transactional
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
