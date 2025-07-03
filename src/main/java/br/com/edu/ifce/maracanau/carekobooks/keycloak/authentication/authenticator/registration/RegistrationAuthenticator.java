package br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.authenticator.registration;

import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.dao.UserDAO;
import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.mapper.UserMapper;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.UUID;

public class RegistrationAuthenticator implements Authenticator {

    private final UserDAO userDAO;

    public RegistrationAuthenticator(KeycloakSession keycloakSession) {
        userDAO = new UserDAO(keycloakSession.getProvider(JpaConnectionProvider.class, "app-storage").getEntityManager());
    }

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        var user = authenticationFlowContext.getUser();
        if (user == null || user.getId() == null) {
            authenticationFlowContext.failure(AuthenticationFlowError.INTERNAL_ERROR);
            return;
        }

        try {
            userDAO.save(UserMapper.from(UUID.fromString(user.getId()), user.getUsername()));
        } catch (Exception e) {
            authenticationFlowContext.getSession().users().removeUser(authenticationFlowContext.getRealm(), user);
            authenticationFlowContext.failure(AuthenticationFlowError.INTERNAL_ERROR);
            return;
        }

        authenticationFlowContext.setUser(user);
        authenticationFlowContext.success();
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {
        // No actions required
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // No additional required actions to set
    }

    @Override
    public void close() {
        // No cleanup required
    }

}
