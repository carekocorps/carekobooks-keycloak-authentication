package br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.authenticator.idp;

import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.mapper.UserMapper;
import br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.persistence.dao.UserDAO;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.broker.AbstractIdpAuthenticator;
import org.keycloak.authentication.authenticators.broker.util.SerializedBrokeredIdentityContext;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.UUID;

public class IdpAuthenticator extends AbstractIdpAuthenticator {

    private final UserDAO userDAO;

    public IdpAuthenticator(KeycloakSession keycloakSession) {
        userDAO = new UserDAO(keycloakSession.getProvider(JpaConnectionProvider.class, "user-storage").getEntityManager());
    }

    @Override
    protected void authenticateImpl(AuthenticationFlowContext authenticationFlowContext, SerializedBrokeredIdentityContext serializedBrokeredIdentityContext, BrokeredIdentityContext brokeredIdentityContext) {
        var brokerEmail = brokeredIdentityContext.getEmail();
        if (brokerEmail == null || brokerEmail.isEmpty()) {
            authenticationFlowContext.failure(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR);
            return;
        }

        var existingUser = authenticationFlowContext.getSession().users().getUserByEmail(authenticationFlowContext.getRealm(), brokerEmail);
        if (existingUser == null) {
            var newUsername = UUID.randomUUID().toString().replace("-", "");
            var newUser = authenticationFlowContext
                    .getSession()
                    .users()
                    .addUser(authenticationFlowContext.getRealm(), newUsername);

            newUser.setEmail(brokerEmail);
            newUser.setEnabled(true);

            try {
                userDAO.save(UserMapper.from(UUID.fromString(newUser.getId()), newUsername));
            } catch (Exception e) {
                authenticationFlowContext.getSession().users().removeUser(authenticationFlowContext.getRealm(), newUser);
                authenticationFlowContext.failure(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR);
                return;
            }

            authenticationFlowContext.setUser(newUser);
            authenticationFlowContext.success();
            return;
        }

        authenticationFlowContext.setUser(existingUser);
        authenticationFlowContext.success();
    }

    @Override
    protected void actionImpl(AuthenticationFlowContext authenticationFlowContext, SerializedBrokeredIdentityContext serializedBrokeredIdentityContext, BrokeredIdentityContext brokeredIdentityContext) {
        // Intentionally left blank: no additional action required after authentication
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

}
