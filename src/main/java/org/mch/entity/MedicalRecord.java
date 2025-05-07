package org.mch.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
public class MedicalRecord extends PanacheEntity {

    // Referencia al paciente (para simplificar, se guarda el ID del paciente)
    public Long patientId;

    // Identificador del médico que atendió o creó el registro
    public Long doctorId;

    // Fecha y hora del registro o consulta
    public LocalDateTime recordDate;

    // Diagnóstico realizado
    public String diagnosis;

    // Tratamiento prescrito
    public String treatment;

    // Notas adicionales
    public String notes;

    public static MedicalRecord findByPatientId(Long patientId){
        return find("patientId", patientId).firstResult();
    }
}

