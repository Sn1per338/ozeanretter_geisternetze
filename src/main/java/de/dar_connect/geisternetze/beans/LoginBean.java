package de.dar_connect.geisternetze.beans;

import de.dar_connect.geisternetze.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.*;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import org.mindrot.jbcrypt.BCrypt;

@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;

    @Inject
    private UserSessionBean userSessionBean;

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("geisternetzePU");

    public String login() {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);

            User user = query.getSingleResult();

            if (user != null && BCrypt.checkpw(password, user.getPassword())) {
                userSessionBean.setCurrentUser(user);

                String targetPage = userSessionBean.getRedirectAfterLogin();
                userSessionBean.clearRedirectAfterLogin();

                if (targetPage != null) {
                    return targetPage + "?faces-redirect=true";
                } else {
                    return "dashboard.xhtml?faces-redirect=true";
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fehlgeschlagen", "Ungültiger Benutzername oder Passwort."));
                return null;
            }

        } catch (NoResultException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fehlgeschlagen", "Benutzer nicht gefunden."));
            return null;

        } finally {
            em.close();
        }
    }

    // Getter & Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
