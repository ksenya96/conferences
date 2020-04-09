package com.conf.conferences.security.logout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Long> {
    InvalidToken getByToken(String token);

    @Override
    <S extends InvalidToken> S saveAndFlush(S entity);

    List<InvalidToken> getAllByExpirationTimeBefore(LocalDateTime date);

    @Override
    void deleteAll(Iterable<? extends InvalidToken> iterable);
}
