package com.example.ergasiapssd.user;

import com.example.ergasiapssd.security.auth.AuthenticationRequest;
import com.example.ergasiapssd.security.auth.AuthenticationResponse;
import com.example.ergasiapssd.security.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId){
        boolean exists = userRepository.existsById(userId);

        if(!exists)
            throw new IllegalStateException("User with id " + userId + "does not exist.");

        userRepository.deleteById(userId);
    }

    public AuthenticationResponse authenticate(User user) {
        return authenticationService.authenticate(
                new AuthenticationRequest(
                        user.getUsername(),
                        user.getPassword()
                )
        );
    }
}
