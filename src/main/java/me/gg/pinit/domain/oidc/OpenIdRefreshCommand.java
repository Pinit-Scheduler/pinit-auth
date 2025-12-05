package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenIdRefreshCommand extends OpenIdCommand {
    private final String refresh_token;

    public OpenIdRefreshCommand(String client_id, String client_secret, String refresh_token) {
        super("refresh_token", client_id, client_secret);
        this.refresh_token = refresh_token;
    }

    @Override
    public List<Oauth2Token> execute(Oauth2Provider provider) {
        return provider.grantToken(this);
    }
}
