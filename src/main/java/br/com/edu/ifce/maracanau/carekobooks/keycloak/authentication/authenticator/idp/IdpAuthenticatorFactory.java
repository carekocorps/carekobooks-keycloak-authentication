package br.com.edu.ifce.maracanau.carekobooks.keycloak.authentication.authenticator.idp;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class IdpAuthenticatorFactory implements AuthenticatorFactory {

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.DISABLED,
            AuthenticationExecutionModel.Requirement.REQUIRED
    };

    private String jdbcUrl;
    private String dbUsername;
    private String dbPassword;

    @Override
    public String getId() {
        return "idp-register";
    }

    @Override
    public String getDisplayType() {
        return "Register User from Identity Provider";
    }

    @Override
    public String getHelpText() {
        return "Registers the user in the local system if they don't exist after logging in with an Identity Provider";
    }

    @Override
    public String getReferenceCategory() {
        return "idp-register";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of(
                new ProviderConfigProperty("jdbcUrl", "JDBC URL", "", ProviderConfigProperty.STRING_TYPE, ""),
                new ProviderConfigProperty("dbUsername", "Database Username", "", ProviderConfigProperty.STRING_TYPE, ""),
                new ProviderConfigProperty("dbPassword", "Database Password", "", ProviderConfigProperty.STRING_TYPE, "")
        );
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new IdpAuthenticator(jdbcUrl, dbUsername, dbPassword);
    }

    @Override
    public void init(Config.Scope scope) {
        jdbcUrl = scope.get("jdbcUrl");
        dbUsername = scope.get("dbUsername");
        dbPassword = scope.get("dbPassword");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // No post-initialization required
    }

    @Override
    public void close() {
        // No cleanup required
    }

}
