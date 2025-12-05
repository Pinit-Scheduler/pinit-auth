package me.gg.pinit.domain.oidc;


import java.util.List;

public interface Oauth2Provider {
    List<Oauth2Token> grantToken(OpenIdCommand command);

    Profile getProfile(Oauth2Token token);
}
