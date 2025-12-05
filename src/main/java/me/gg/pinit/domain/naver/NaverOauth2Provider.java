package me.gg.pinit.domain.naver;

import me.gg.pinit.domain.OpenIdCommand;
import me.gg.pinit.domain.Profile;

public interface NaverOauth2Provider {
    NaverOauth2Token getToken(OpenIdCommand command);

    Profile getProfile(OpenIdCommand command);
}
