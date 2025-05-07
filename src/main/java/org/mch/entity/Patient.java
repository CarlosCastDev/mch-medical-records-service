package org.mch.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class Patient extends PanacheEntity {
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public LocalDate dateOfBirth;
}

