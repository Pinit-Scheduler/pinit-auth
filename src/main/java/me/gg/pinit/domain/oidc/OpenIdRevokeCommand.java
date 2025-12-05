package me.gg.pinit.domain.oidc;

public class OpenIdRevokeCommand extends OpenIdCommand {
    private String access_token;
    private String service_provider;

    public OpenIdRevokeCommand(String grant_type, String client_id, String client_secret) {
        super(grant_type, client_id, client_secret);
    }

    @Override
    public void execute() {

    }
}
