package me.gg.pinit.domain.member;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class OauthAccount {
    @EmbeddedId
    private OauthAccountId id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    protected OauthAccount() {
    }

    public OauthAccount(OauthAccountId id, Member member) {
        this.id = id;
        this.member = member;
    }
}
