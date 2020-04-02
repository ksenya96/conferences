package com.conf.conferences.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UserDetails loadUserByUsernameAndOauth2Resource(String username, SocialType socialType)
            throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.findByUsernameAndOauth2Resource(username, socialType))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username +
                                                                 " from resource: " + socialType));
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}