package de.dar_connect.geisternetze.beans;

import de.dar_connect.geisternetze.model.User;
import de.dar_connect.geisternetze.model.Rolle;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.mindrot.jbcrypt.BCrypt;

@Named
@RequestScoped
public class UserBean {

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;

    private Rolle rolle = Rolle.MELDENDE_PERSON;
    private boolean anonym = false;

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("geisternetzePU");

    public String register() {
        System.out.println("---- REGISTRIERUNG WIRD AUSGEFÜHRT ----");
        System.out.println("Benutzername: " + username);
        System.out.println("Passwort: " + password);
        System.out.println("Bestätigung: " + confirmPassword);

        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwörter stimmen nicht überein.", null));
            return null;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            if (isUsernameOrEmailTaken(em, username, email)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Benutzername oder E-Mail bereits vergeben.", null));
                em.getTransaction().rollback();
                return null;
            }

            User newUser = new User();
            newUser.setFirstname(firstname);
            newUser.setLastname(lastname);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            newUser.setRolle(rolle);
            newUser.setAnonym(anonym);

            em.persist(newUser);
            em.getTransaction().commit();

            return "login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registrierung fehlgeschlagen.", null));
            return null;
        } finally {
            em.close();
        }
    }

    private boolean isUsernameOrEmailTaken(EntityManager em, String username, String email) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username OR u.email = :email", Long.class)
            .setParameter("username", username)
            .setParameter("email", email)
            .getSingleResult();
        return count > 0;
    }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Rolle getRolle() { return rolle; }
    public void setRolle(Rolle rolle) { this.rolle = rolle; }

    public boolean isAnonym() { return anonym; }
    public void setAnonym(boolean anonym) { this.anonym = anonym; }
}
