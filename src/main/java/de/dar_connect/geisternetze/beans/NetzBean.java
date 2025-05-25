package de.dar_connect.geisternetze.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dar_connect.geisternetze.dto.GeisternetzDTO;
import de.dar_connect.geisternetze.model.Geisternetze;
import de.dar_connect.geisternetze.model.Status;
import de.dar_connect.geisternetze.model.StatusAenderung;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.*;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named("netzBean")
@RequestScoped
public class NetzBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long geisternetzeIdAktion;

    private double standortLaengengrad;
    private double standortBreitengrad;
    private double groesseInQuadratmeter;

    private String successMessage;

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("geisternetzePU");

    @Inject
    private UserSessionBean userSessionBean;

    private List<Geisternetze> getAlleNetze() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT g FROM Geisternetze g LEFT JOIN FETCH g.statusHistorie", Geisternetze.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Geisternetze> getOffeneNetze() {
        return getAlleNetze().stream()
                .filter(netz -> {
                    Status status = netz.getAktuellerStatus();
                    return status != null && status != Status.GEBORGEN && status != Status.VERSCHOLLEN;
                })
                .collect(Collectors.toList());
    }

    public List<Geisternetze> getGeborgeneNetze() {
        return getAlleNetze().stream()
                .filter(netz -> Status.GEBORGEN.equals(netz.getAktuellerStatus()))
                .collect(Collectors.toList());
    }

    public List<Geisternetze> getGemeldeteNetzeByUser() {
        return getAlleNetze().stream()
                .filter(netz -> userSessionBean.getCurrentUser() != null &&
                        userSessionBean.getCurrentUser().equals(netz.getMeldendePerson()))
                .collect(Collectors.toList());
    }

    public List<Geisternetze> getUebernommeneBergungenByUser() {
        return getAlleNetze().stream()
                .filter(netz -> userSessionBean.getCurrentUser() != null &&
                        userSessionBean.getCurrentUser().equals(netz.getBergendePerson()) &&
                        Status.BERGUNG_BEVORSTEHEND.equals(netz.getAktuellerStatus()))
                .collect(Collectors.toList());
    }

    public List<Geisternetze> getGeborgeneNetzeByUser() {
        return getAlleNetze().stream()
                .filter(netz -> userSessionBean.getCurrentUser() != null &&
                        userSessionBean.getCurrentUser().equals(netz.getBergendePerson()) &&
                        Status.GEBORGEN.equals(netz.getAktuellerStatus()))
                .collect(Collectors.toList());
    }

    public List<Geisternetze> getVerscholleneNetzeByUser() {
        return getAlleNetze().stream()
                .filter(netz -> userSessionBean.getCurrentUser() != null &&
                        netz.getStatusHistorie().stream()
                                .anyMatch(a -> a.getStatus() == Status.VERSCHOLLEN &&
                                        userSessionBean.getCurrentUser().equals(a.getAenderndePerson())))
                .collect(Collectors.toList());
    }

    public String updateStatus(String statusString) {
        Status neuerStatus = Status.valueOf(statusString);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Geisternetze netz = em.find(Geisternetze.class, geisternetzeIdAktion);
            if (netz != null) {
                if (neuerStatus == Status.BERGUNG_BEVORSTEHEND) {
                    netz.setBergendePerson(userSessionBean.getCurrentUser());
                }

                StatusAenderung aenderung = new StatusAenderung();
                aenderung.setStatus(neuerStatus);
                aenderung.setZeitpunkt(new Date());
                aenderung.setGeisternetz(netz);
                aenderung.setAenderndePerson(userSessionBean.getCurrentUser());

                em.persist(aenderung);
            }

            em.getTransaction().commit();
            return "geisternetze_managen.xhtml?faces-redirect=true";
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public String submit() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Geisternetze netz = new Geisternetze();
            netz.setMeldedatum(new Date());
            netz.setStandortLaengengrad(standortLaengengrad);
            netz.setStandortBreitengrad(standortBreitengrad);
            netz.setGroesseInQuadratmeter(groesseInQuadratmeter);

            if (userSessionBean.isLoggedIn()) {
                netz.setMeldendePerson(userSessionBean.getCurrentUser());
            }

            StatusAenderung aenderung = new StatusAenderung();
            aenderung.setStatus(Status.GEMELDET);
            aenderung.setZeitpunkt(new Date());
            aenderung.setGeisternetz(netz);

            if (userSessionBean.isLoggedIn()) {
                aenderung.setAenderndePerson(userSessionBean.getCurrentUser());
            }

            em.persist(netz);
            em.persist(aenderung);
            em.getTransaction().commit();

            successMessage = "Das Geisternetz wurde erfolgreich gemeldet! Vielen Dank für deinen Beitrag.";
            return null;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            successMessage = "Fehler beim Melden des Geisternetzes. Bitte versuche es erneut.";
            return null;
        } finally {
            em.close();
        }
    }

    public String getGeisternetzeJson() {
        List<Geisternetze> netze = getAlleNetze().stream()
            .filter(netz -> {
                Status status = netz.getAktuellerStatus();
                return status != Status.GEBORGEN;
            })
            .collect(Collectors.toList());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        List<GeisternetzDTO> dtoList = netze.stream().map(netz -> new GeisternetzDTO(
            netz.getStandortLaengengrad(),
            netz.getStandortBreitengrad(),
            netz.getGroesseInQuadratmeter(),
            netz.getAktuellerStatus() != null ? netz.getAktuellerStatus().name() : "UNBEKANNT",
            sdf.format(netz.getMeldedatum())
        )).collect(Collectors.toList());

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(dtoList);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }


    public Long getGeisternetzeIdAktion() { return geisternetzeIdAktion; }
    public void setGeisternetzeIdAktion(Long id) { this.geisternetzeIdAktion = id; }

    public double getStandortLaengengrad() { return standortLaengengrad; }
    public void setStandortLaengengrad(double l) { this.standortLaengengrad = l; }

    public double getStandortBreitengrad() { return standortBreitengrad; }
    public void setStandortBreitengrad(double b) { this.standortBreitengrad = b; }

    public double getGroesseInQuadratmeter() { return groesseInQuadratmeter; }
    public void setGroesseInQuadratmeter(double g) { this.groesseInQuadratmeter = g; }

    public String getSuccessMessage() { return successMessage; }
    public void setSuccessMessage(String m) { this.successMessage = m; }
}