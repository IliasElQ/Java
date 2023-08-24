package com.example.javfx;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

public class PatientDetails {
    private int id;
    private int patientId;



    public PatientDetails(int id, int patientId) {
        this.id = id;
        this.patientId = patientId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    private String remarque;
    private LocalDate date1;
    private LocalDate date2;

    public PatientDetails( LocalDate date1,String remarque, LocalDate date2) {
        this.remarque = remarque;
        this.date1 = date1;
        this.date2 = date2;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public LocalDate getDate1() {
        return date1;
    }

    public void setDate1(LocalDate date1) {
        this.date1 = date1;
    }

    public LocalDate getDate2() {
        return date2;
    }

    public void setDate2(LocalDate date2) {
        this.date2 = date2;
    }

    private ZonedDateTime date;



    public PatientDetails(LocalDate date2, String remarque) {
        this.date2 = date2;
        this.remarque = remarque;
    }



}


