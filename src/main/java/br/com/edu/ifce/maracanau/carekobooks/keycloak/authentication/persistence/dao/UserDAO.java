package br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.dao;

import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDAO {

    private final EntityManager em;

    @Transactional
    public void save(User user) {
        em.persist(user);
    }

}
