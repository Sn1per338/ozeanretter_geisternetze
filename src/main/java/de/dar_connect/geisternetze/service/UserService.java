package de.dar_connect.geisternetze.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class UserService {

    private static final EntityManagerFactory emf =
        Persistence.createEntityManagerFactory("geisternetzePU");

    public boolean isUsernameOrEmailTaken(String username, String email) {
        EntityManager em = emf.createEntityManager();

        try {
            Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username OR u.email = :email",
                Long.class)
                .setParameter("username", username)
                .setParameter("email", email)
                .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }
}
