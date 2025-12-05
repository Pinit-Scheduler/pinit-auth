package me.gg.pinit.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginResult {
    private String state;
    private String code;
    private String client_id;
    private String redirect_uri;
}
