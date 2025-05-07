package org.mch.rest;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.mch.entity.MedicalRecord;

import java.util.List;

@Path("/records")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicalRecordResource {

    // Obtener todos los registros de un paciente específico
    @GET
    @Path("/patient/{patientId}")
    public List<MedicalRecord> getRecordsByPatient(@PathParam("patientId") Long patientId) {
        return MedicalRecord.list("patientId", patientId);
    }

    // Obtener un registro médico por su ID
    @GET
    @Path("/{id}")
    public MedicalRecord getRecord(@PathParam("id") Long id) {
        MedicalRecord record = MedicalRecord.findById(id);
        if (record == null) {
            throw new NotFoundException("Registro médico no encontrado");
        }
        return record;
    }

    // Crear un nuevo registro médico
    @POST
    @Transactional
    public Response createRecord(MedicalRecord record, @Context UriInfo uriInfo) {
        record.persist();
        if (record.isPersistent()) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(record.id.toString());
            return Response.created(builder.build()).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // Actualizar un registro médico existente
    @PUT
    @Path("/{id}")
    @Transactional
    public MedicalRecord updateRecord(@PathParam("id") Long id, MedicalRecord updatedRecord) {
        MedicalRecord record = MedicalRecord.findById(id);
        if (record == null) {
            throw new NotFoundException("Registro médico no encontrado");
        }
        record.patientId = updatedRecord.patientId;
        record.doctorId = updatedRecord.doctorId;
        record.recordDate = updatedRecord.recordDate;
        record.diagnosis = updatedRecord.diagnosis;
        record.treatment = updatedRecord.treatment;
        record.notes = updatedRecord.notes;
        return record;
    }

    // Eliminar un registro médico
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteRecord(@PathParam("id") Long id) {
        MedicalRecord record = MedicalRecord.findById(id);
        if (record == null) {
            throw new NotFoundException("Registro médico no encontrado");
        }
        record.delete();
        return Response.noContent().build();
    }
}

