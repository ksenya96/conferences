package com.conf.conferences.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameAndOauth2Resource(String username, SocialType socialType);
    User findByEmail(String email);

    @Override
    <S extends User> S saveAndFlush(S entity);
}
