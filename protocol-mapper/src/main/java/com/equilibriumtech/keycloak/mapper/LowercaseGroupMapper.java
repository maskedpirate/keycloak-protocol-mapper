package com.equilibriumtech.keycloak.mapper;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Our own example protocol mapper.
 */
public class LowercaseGroupMapper extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    /*
     * A config which keycloak uses to display a generic dialog to configure the token.
     */
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    /*
     * The ID of the token mapper. Is public, because we need this id in our data-setup project to
     * configure the protocol mapper in keycloak.
     */
    public static final String PROVIDER_ID = "oidc-hello-world-mapper";

    static {
        // The builtin protocol mapper let the user define under which claim name (key)
        // the protocol mapper writes its value. To display this option in the generic dialog
        // in keycloak, execute the following method.
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        // The builtin protocol mapper let the user define for which tokens the protocol mapper
        // is executed (access token, id token, user info). To add the config options for the different types
        // to the dialog execute the following method. Note that the following method uses the interfaces
        // this token mapper implements to decide which options to add to the config. So if this token
        // mapper should never be available for some sort of options, e.g. like the id token, just don't
        // implement the corresponding interface.
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, LowercaseGroupMapper.class);
    }

    @Override
    public String getDisplayCategory() {
        return "Token mapper";
    }

    @Override
    public String getDisplayType() {
        return "Hello World Mapper";
    }

    @Override
    public String getHelpText() {
        return "Adds a hello world text to the claim";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    protected void setClaim(final IDToken token,
                            final ProtocolMapperModel mappingModel,
                            final UserSessionModel userSession,
                            final KeycloakSession keycloakSession,
                            final ClientSessionContext clientSessionCtx) {

        // Retrieve the user groups
        Set<String> lowercaseGroups = userSession.getUser().getGroupsStream()
            .map(g -> g.getName())
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        // Set the claim in the token
        OIDCAttributeMapperHelper.mapClaim(token, mappingModel, lowercaseGroups);
    }

}
