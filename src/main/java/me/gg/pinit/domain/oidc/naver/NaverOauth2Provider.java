package me.gg.pinit.domain.oidc.naver;

import me.gg.pinit.domain.oidc.OpenIdCommand;
import me.gg.pinit.domain.oidc.Profile;

public interface NaverOauth2Provider {
    NaverOauth2Token getToken(OpenIdCommand command);

    Profile getProfile(OpenIdCommand command);
}
