package me.gg.pinit.domain.oidc;

public class OpenIdPublishCommand extends OpenIdCommand {
    private String code;
    private String state;

    public OpenIdPublishCommand(String grant_type, String client_id, String client_secret) {
        super(grant_type, client_id, client_secret);
    }

    @Override
    public void execute(Oauth2Provider provider) {

    }
}
