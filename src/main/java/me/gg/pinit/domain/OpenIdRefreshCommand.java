package me.gg.pinit.domain;

public class OpenIdRefreshCommand extends OpenIdCommand {
    private String refresh_token;

    public OpenIdRefreshCommand(String grant_type, String client_id, String client_secret) {
        super(grant_type, client_id, client_secret);
    }

    @Override
    public void execute() {

    }
}
