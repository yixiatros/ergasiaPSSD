package com.example.ergasiapssd.user;

import com.example.ergasiapssd.security.LogoutService;
import com.example.ergasiapssd.security.auth.AuthenticationRequest;
import com.example.ergasiapssd.security.auth.AuthenticationResponse;
import com.example.ergasiapssd.security.auth.AuthenticationService;
import com.example.ergasiapssd.security.auth.RegisterRequest;
import com.example.ergasiapssd.user.role.Role;
import com.example.ergasiapssd.user.role.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;

    @Autowired
    public UserService(UserRepository userRepository,
                       AuthenticationService authenticationService,
                       LogoutService logoutService,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.logoutService = logoutService;
        this.roleRepository = roleRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<String> getStudentsUsernames() { return userRepository.findUsernamesByRole("ROLE_STUDENT"); }

    public List<String> getStudentsUsernames(String search) { return userRepository.findUsernamesByRoleViaSearch("ROLE_STUDENT", search); }

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

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        logoutService.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        CookieClearingLogoutHandler cookieClearingLogoutHandler = new CookieClearingLogoutHandler(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        cookieClearingLogoutHandler.logout(request, response, null);
        securityContextLogoutHandler.logout(request, response, null);
    }

    public AuthenticationResponse registerUser(User user, String roleName) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        Optional<User> userOptional1 = userRepository.findUserByUsername(user.getUsername());

        if (userOptional.isPresent())
            return AuthenticationResponse.builder().accessToken("null").refreshToken("Email taken.").build();

        if (userOptional1.isPresent())
            return AuthenticationResponse.builder().accessToken(null).refreshToken("Username taken.").build();

        Optional<Role> roleOptional = roleRepository.findRoleByName(roleName);
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
