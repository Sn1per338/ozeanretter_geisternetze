package de.dar_connect.geisternetze.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "status_aenderungen")
public class StatusAenderung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date zeitpunkt;

    @ManyToOne
    @JoinColumn(name = "geisternetz_id", nullable = false)
    private Geisternetze geisternetz;

    @ManyToOne
    @JoinColumn(name = "benutzer_id")
    private User aenderndePerson;

    private String kommentar;


    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getZeitpunkt() {
        return zeitpunkt;
    }

    public void setZeitpunkt(Date zeitpunkt) {
        this.zeitpunkt = zeitpunkt;
    }

    public Geisternetze getGeisternetz() {
        return geisternetz;
    }

    public void setGeisternetz(Geisternetze geisternetz) {
        this.geisternetz = geisternetz;
    }

    public User getAenderndePerson() {
        return aenderndePerson;
    }

    public void setAenderndePerson(User aenderndePerson) {
        this.aenderndePerson = aenderndePerson;
    }

    public String getKommentar() {
        return kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }
}
