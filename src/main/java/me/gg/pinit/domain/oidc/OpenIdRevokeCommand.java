package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenIdRevokeCommand extends OpenIdCommand {
    private final String access_token;
    private final String service_provider;

    public OpenIdRevokeCommand(String client_id, String client_secret, String access_token, String service_provider) {
        super("delete", client_id, client_secret);
        this.access_token = access_token;
        this.service_provider = service_provider;
    }

    @Override
    public List<Oauth2Token> execute(Oauth2Provider provider) {
        return provider.grantToken(this);
    }
}
