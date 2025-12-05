package me.gg.pinit.domain.member;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OauthAccount {
    @EmbeddedId
    private OauthAccountId id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
