package com.example.demo.apps;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtils {
    private static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("my-persistence-unit");

    public static <R> R doInJPA(EntityManagerFunction<R> action) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            R result = action.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Re-throw the exception after rollback
        } finally {
            em.close();
        }
    }

    @FunctionalInterface
    public interface EntityManagerFunction<R> {
        R apply(EntityManager em);
    }
}
