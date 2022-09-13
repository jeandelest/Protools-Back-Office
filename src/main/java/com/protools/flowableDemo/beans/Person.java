package com.protools.flowableDemo.beans;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Person {
    private Long id;
    private String nom;
    private String prenom;
    private String email;

    private Long idSurvey;
    public Person() {

    }
    public Person(String nom, String prenom, String email){
        this.email =email;
        this.nom = nom;
        this.prenom = prenom;

    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Long getIdSurvey() {
        return idSurvey;
    }

    public void setIdSurvey(Long id_survey) {
        this.idSurvey = id_survey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return nom.equals(person.nom) && prenom.equals(person.prenom) && email.equals(person.email) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, prenom, email);
    }

    @Override
    public String toString() {
        return "Person{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +

                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }
}
