package me.gg.pinit.domain;

import jakarta.persistence.*;

@Entity
public class OauthAccount {
    @EmbeddedId
    private OauthAccountId id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
