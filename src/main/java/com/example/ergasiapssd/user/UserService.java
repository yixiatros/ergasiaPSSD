package com.example.ergasiapssd.user;

import com.example.ergasiapssd.security.auth.AuthenticationRequest;
import com.example.ergasiapssd.security.auth.AuthenticationResponse;
import com.example.ergasiapssd.security.auth.AuthenticationService;
import com.example.ergasiapssd.security.auth.RegisterRequest;
import com.example.ergasiapssd.user.role.Role;
import com.example.ergasiapssd.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       AuthenticationService authenticationService,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.roleRepository = roleRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
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

    public AuthenticationResponse registerUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        Optional<User> userOptional1 = userRepository.findUserByUsername(user.getUsername());

        if (userOptional.isPresent())
            return AuthenticationResponse.builder().accessToken("null").refreshToken("Email taken.").build();

        if (userOptional1.isPresent())
            return AuthenticationResponse.builder().accessToken(null).refreshToken("Username taken.").build();

        Optional<Role> roleOptional = roleRepository.findRoleByName("ROLE_USER");
        if (roleOptional.isEmpty())
            return AuthenticationResponse.builder().accessToken(null).refreshToken("Something went wrong.").build();

        user.addRole(roleOptional.get());

        return authenticationService.register(
                new RegisterRequest(
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail(),
                        //Arrays.asList(roleOptional.get())
                        user.getRoles()
                )
        );
    }
}
