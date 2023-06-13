package com.module.bpmn.security.jwt;

import com.module.bpmn.model.User;
import com.module.bpmn.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "customUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByUsername(username);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            System.err.println("here");
            return new UserDetailsImpl(user);
        }
        System.err.println("here 2");
        throw new UsernameNotFoundException("User not found");
    }
}
