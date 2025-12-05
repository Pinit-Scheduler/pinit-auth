package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class OpenIdCommand {
    private final String grant_type;
    private final String client_id;
    private final String client_secret;

    public OpenIdCommand(String grant_type, String client_id, String client_secret) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    public abstract List<Oauth2Token> execute(Oauth2Provider provider);
}