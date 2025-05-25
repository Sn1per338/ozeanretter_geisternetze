package de.dar_connect.geisternetze.beans;

import de.dar_connect.geisternetze.model.Rolle;
import de.dar_connect.geisternetze.model.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;

import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {

    private User currentUser;

    private String redirectAfterLogin;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUsername() {
        return isLoggedIn() ? currentUser.getUsername() : "Gast";
    }

    public String getRedirectAfterLogin() {
        return redirectAfterLogin;
    }

    public void setRedirectAfterLogin(String redirectAfterLogin) {
        this.redirectAfterLogin = redirectAfterLogin;
    }

    public void clearRedirectAfterLogin() {
        this.redirectAfterLogin = null;
    }

    public void redirectIfNotLoggedIn() throws IOException {
        if (!isLoggedIn()) {
            String currentPage = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            setRedirectAfterLogin(currentPage);
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        }
    }
    
    public void logout() throws IOException {
        currentUser = null;
        redirectAfterLogin = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public boolean isAnonym() {
        return isLoggedIn() && currentUser.isAnonym();
    }

    public boolean isMeldendePerson() {
        return isLoggedIn() && Rolle.MELDENDE_PERSON.equals(currentUser.getRolle());
    }

    public boolean isBergendePerson() {
        return isLoggedIn() && Rolle.BERGENDE_PERSON.equals(currentUser.getRolle());
    }
}
