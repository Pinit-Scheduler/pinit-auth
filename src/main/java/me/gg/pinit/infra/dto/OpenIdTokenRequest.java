package me.gg.pinit.infra.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenIdTokenRequest {
    private String grant_type;
    private String client_id;
    private String client_secret;
    private String code;
    private String state;
    private String refresh_token;
    private String access_token;
}
