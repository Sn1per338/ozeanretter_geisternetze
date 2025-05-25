package de.dar_connect.geisternetze.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "geisternetze")
public class Geisternetze {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date meldedatum;

    private double standortLaengengrad;
    private double standortBreitengrad;

    private double groesseInQuadratmeter;

    @ManyToOne
    @JoinColumn(name = "meldende_person_id", nullable = true)
    private User meldendePerson;

    @ManyToOne
    @JoinColumn(name = "bergende_person_id", nullable = true)
    private User bergendePerson;

    @OneToMany(mappedBy = "geisternetz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StatusAenderung> statusHistorie = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Date getMeldedatum() {
        return meldedatum;
    }

    public void setMeldedatum(Date meldedatum) {
        this.meldedatum = meldedatum;
    }

    public double getStandortLaengengrad() {
        return standortLaengengrad;
    }

    public void setStandortLaengengrad(double standortLaengengrad) {
        this.standortLaengengrad = standortLaengengrad;
    }

    public double getStandortBreitengrad() {
        return standortBreitengrad;
    }

    public void setStandortBreitengrad(double standortBreitengrad) {
        this.standortBreitengrad = standortBreitengrad;
    }

    public double getGroesseInQuadratmeter() {
        return groesseInQuadratmeter;
    }

    public void setGroesseInQuadratmeter(double groesseInQuadratmeter) {
        this.groesseInQuadratmeter = groesseInQuadratmeter;
    }

    public User getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(User meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public User getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(User bergendePerson) {
        this.bergendePerson = bergendePerson;
    }

    public List<StatusAenderung> getStatusHistorie() {
        return statusHistorie;
    }

    public void setStatusHistorie(List<StatusAenderung> statusHistorie) {
        this.statusHistorie = statusHistorie;
    }

    @Transient
    public Status getAktuellerStatus() {
        return statusHistorie == null || statusHistorie.isEmpty()
                ? null
                : statusHistorie.stream()
                .max(Comparator.comparing(StatusAenderung::getZeitpunkt))
                .map(StatusAenderung::getStatus)
                .orElse(null);
    }

    @Transient
    public Status getStatus() {
        return getAktuellerStatus();
    }

    @Transient
    public User getLetzteAenderndePerson() {
        return statusHistorie == null || statusHistorie.isEmpty()
                ? null
                : statusHistorie.stream()
                .max(Comparator.comparing(StatusAenderung::getZeitpunkt))
                .map(StatusAenderung::getAenderndePerson)
                .orElse(null);
    }

    @Transient
    public String getLetzteAenderndePersonName() {
        User person = getLetzteAenderndePerson();
        return person != null
                ? person.getFirstname() + " " + person.getLastname()
                : "—";
    }
}
