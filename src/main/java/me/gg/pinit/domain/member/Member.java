package me.gg.pinit.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;

    @Setter
    private boolean socialLogin = false;

    protected Member() {
    }

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
