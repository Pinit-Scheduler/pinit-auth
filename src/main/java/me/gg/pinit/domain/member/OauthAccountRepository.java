package me.gg.pinit.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, OauthAccountId> {
}
