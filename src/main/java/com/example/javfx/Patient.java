package com.example.javfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.time.LocalDate;
import java.util.Objects;

public class Patient extends Button {

    private int patientId;





    private String Id_nat;
    private String name;
    private String prenam;
    private int age;
    private int phone;
    private LocalDate date;
    private String gender;
    private String type_maladie;
    private String remarque;
    private LocalDate date1;
    private LocalDate date2;

    private final StringProperty remarqueProperty = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date1Property = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date2Property = new SimpleObjectProperty<>();

    public Patient( LocalDate date1,String remarque, LocalDate date2) {
        this.date1 = date1;
        this.remarque = remarque;
        this.date2 = date2;
    }
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }





    public Patient(String s, Node node, String remarque, LocalDate date1, LocalDate date2) {
        super(s, node);
        this.remarque = remarque;
        this.date1 = date1;
        this.date2 = date2;
    }

    public Patient(String id_nat, String name, String prenam, int age, int phone, LocalDate date, String gender, String type_maladie, String remarque, LocalDate date1, LocalDate date2) {
        Id_nat = id_nat;
        this.name = name;
        this.prenam = prenam;
        this.age = age;
        this.phone = phone;
        this.date = date;
        this.gender = gender;
        this.type_maladie = type_maladie;
        this.remarque = remarque;
        this.date1 = date1;
        this.date2 = date2;
    }

    public Patient(String id_nat, String name, String prenam, int age, int phone, LocalDate date, String gender, String type_maladie) {
        Id_nat = id_nat;
        this.name = name;
        this.prenam = prenam;
        this.age = age;
        this.phone = phone;
        this.date = date;
        this.gender = gender;
        this.type_maladie = type_maladie;
    }

    public Patient(String name, String prenam) {
        this.name = name;
        this.prenam = prenam;
    }



    // Getters and setters for all fields
    // ...

    // Getters for property objects
    public StringProperty remarqueProperty() {
        return remarqueProperty;
    }

    public ObjectProperty<LocalDate> date1Property() {
        return date1Property;
    }

    public ObjectProperty<LocalDate> date2Property() {
        return date2Property;
    }

    public  String getId_nat() {
        return Id_nat;
    }

    public void setId_nat(String id_nat) {
        Id_nat = id_nat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrenam() {
        return prenam;
    }

    public void setPrenam(String prenam) {
        this.prenam = prenam;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType_maladie() {
        return type_maladie;
    }

    public void setType_maladie(String type_maladie) {
        this.type_maladie = type_maladie;
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

    public String getRemarqueProperty() {
        return remarqueProperty.get();
    }

    public StringProperty remarquePropertyProperty() {
        return remarqueProperty;
    }

    public void setRemarqueProperty(String remarqueProperty) {
        this.remarqueProperty.set(remarqueProperty);
    }

    public LocalDate getDate1Property() {
        return date1Property.get();
    }

    public ObjectProperty<LocalDate> date1PropertyProperty() {
        return date1Property;
    }

    public void setDate1Property(LocalDate date1Property) {
        this.date1Property.set(date1Property);
    }

    public LocalDate getDate2Property() {
        return date2Property.get();
    }

    public ObjectProperty<LocalDate> date2PropertyProperty() {
        return date2Property;
    }

    public void setDate2Property(LocalDate date2Property) {
        this.date2Property.set(date2Property);
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return getAge() == patient.getAge() && getPhone() == patient.getPhone() && Objects.equals(getId_nat(), patient.getId_nat()) && Objects.equals(getName(), patient.getName()) && Objects.equals(getPrenam(), patient.getPrenam()) && Objects.equals(getDate(), patient.getDate()) && Objects.equals(getGender(), patient.getGender()) && Objects.equals(getType_maladie(), patient.getType_maladie()) && Objects.equals(getRemarque(), patient.getRemarque()) && Objects.equals(getDate1(), patient.getDate1()) && Objects.equals(getDate2(), patient.getDate2()) && Objects.equals(getRemarqueProperty(), patient.getRemarqueProperty()) && Objects.equals(getDate1Property(), patient.getDate1Property()) && Objects.equals(getDate2Property(), patient.getDate2Property());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId_nat(), getName(), getPrenam(), getAge(), getPhone(), getDate(), getGender(), getType_maladie(), getRemarque(), getDate1(), getDate2(), getRemarqueProperty(), getDate1Property(), getDate2Property());
    }





}

