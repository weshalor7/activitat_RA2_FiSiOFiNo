package com.fisiofino.model;

import java.time.LocalDate;

/*
 * Model (la "M" de MVC): representa un pacient de la clínica.
 * Cada objecte equival a una fila de la taula patient de la base de dades.
 *
 * És una classe de dades simple: té atributs, constructor, getters i toString, però cap
 * lògica ni accés a la base de dades (d'això s'ocupa GestioPacients).
 */

public class Pacient {

    private String givenName;
    private String familyName;
    private String dni;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String telephone;
    private String email;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Pacient [dni=" + dni + ", givenName=" + givenName + ", familyName=" + familyName
                + ", birthDate=" + birthDate + ", gender=" + gender + ", address=" + address
                + ", city=" + city + ", postalCode=" + postalCode + ", country=" + country
                + ", telephone=" + telephone + ", email=" + email + "]";
    }
}
