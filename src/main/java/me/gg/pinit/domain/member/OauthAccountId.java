package me.gg.pinit.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class OauthAccountId {
    @Column(name = "issuer_uri")
    private String issuerURI;
    @Column(name = "sub")
    private String sub;

    protected OauthAccountId() {
    }

    public OauthAccountId(String issuerURI, String sub) {
        this.issuerURI = issuerURI;
        this.sub = sub;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OauthAccountId that = (OauthAccountId) o;
        return Objects.equals(issuerURI, that.issuerURI) && Objects.equals(sub, that.sub);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuerURI, sub);
    }
}
