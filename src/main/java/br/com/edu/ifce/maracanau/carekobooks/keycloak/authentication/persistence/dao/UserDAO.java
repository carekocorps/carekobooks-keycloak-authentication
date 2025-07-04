package br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.dao;

import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.model.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDAO {

    private final EntityManager entityManager;

    public void save(User user) {
        var transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            entityManager.close();
        }
    }

}
