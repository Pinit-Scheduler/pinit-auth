package me.gg.pinit.domain.oidc;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenIdPublishCommand extends OpenIdCommand {
    private final String code;
    private final String state;

    public OpenIdPublishCommand(String client_id, String client_secret, String code, String state) {
        super("authorization_code", client_id, client_secret);
        this.code = code;
        this.state = state;
    }

    @Override
    public List<Oauth2Token> execute(Oauth2Provider provider) {
        return provider.grantToken(this);
    }
}
