package org.mch.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentEvent {
    private String appointmentId;
    private Long patientId;
    private String doctorId;
    private String status; // Ejemplo: "CREATED", "UPDATED", "CANCELLED"
    private LocalDateTime appointmentDate;
}
