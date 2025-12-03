package me.gg.pinit.controller.dto;

public class LoginResponse {
    private String accessToken;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() {
        return accessToken;
    }
}
