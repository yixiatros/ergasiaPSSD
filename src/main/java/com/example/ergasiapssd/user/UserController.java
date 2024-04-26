package com.example.ergasiapssd.user;

import com.example.ergasiapssd.security.auth.AuthenticationRequest;
import com.example.ergasiapssd.security.auth.AuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Controller
@RequestMapping(path = "users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "/{userId}/delete")
    public RedirectView deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return new RedirectView("/index");
    }

    @GetMapping(path = "/login")
    public String login(Model model) {
        model.addAttribute("newUser", new User());

        return "login";
    }

    @GetMapping(path = "/loginToAccess")
    public RedirectView loginToAccess(Model model) {
        return new RedirectView("/users/login");
    }

    @PostMapping(path = "/login")
    public RedirectView authenticate(@ModelAttribute User newUser) {
        AuthenticationResponse authenticationResponse = userService.authenticate(newUser);

        if (authenticationResponse.getAccessToken() == null) {
            return new RedirectView("/users/login");
        }

        return new RedirectView("/index");
    }
}
