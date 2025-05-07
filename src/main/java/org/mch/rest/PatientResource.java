package org.mch.rest;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.mch.entity.Patient;

import java.util.List;

@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {

    @GET
    public List<Patient> getAllPatients() {
        return Patient.listAll();
    }

    @GET
    @Path("/{id}")
    public Patient getPatient(@PathParam("id") Long id) {
        Patient patient = Patient.findById(id);
        if (patient == null) {
            throw new NotFoundException("Paciente no encontrado");
        }
        return patient;
    }

    @POST
    @Transactional
    public Response createPatient(Patient patient, @Context UriInfo uriInfo) {
        patient.persist();
        if (patient.isPersistent()) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(patient.id.toString());
            return Response.created(builder.build()).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Patient updatePatient(@PathParam("id") Long id, Patient updatedPatient) {
        Patient patient = Patient.findById(id);
        if (patient == null) {
            throw new NotFoundException("Paciente no encontrado");
        }
        patient.firstName = updatedPatient.firstName;
        patient.lastName = updatedPatient.lastName;
        patient.email = updatedPatient.email;
        patient.phone = updatedPatient.phone;
        patient.dateOfBirth = updatedPatient.dateOfBirth;
        return patient;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletePatient(@PathParam("id") Long id) {
        Patient patient = Patient.findById(id);
        if (patient == null) {
            throw new NotFoundException("Paciente no encontrado");
        }
        patient.delete();
        return Response.noContent().build();
    }
}

