package com.conf.conferences.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public UserDetails loadUserByUsernameAndOauth2Resource(String username, SocialType socialType)
            throws UsernameNotFoundException {
        return userRepository.findByUsernameAndOauth2Resource(username, socialType);
    }

    public UserDetails saveAndFlush(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}