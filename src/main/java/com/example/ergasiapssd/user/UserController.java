package com.example.ergasiapssd.user;

import com.example.ergasiapssd.GreekToEnglish;
import com.example.ergasiapssd.security.auth.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(path = "users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    @ResponseBody
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "/studentUsernames")
    @ResponseBody
    public List<String> studentUsernames(@RequestParam(value = "term", required = false, defaultValue = "") String term) {
        List<String> students = new ArrayList<>();

        if (term.equals(""))
            students = userService.getStudentsUsernames();
        else
            students = userService.getStudentsUsernames(GreekToEnglish.convert(term));

        return students;
    }

    @GetMapping(path = "/{userId}/delete")
    public RedirectView deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return new RedirectView("/index");
    }

    @GetMapping(path = "/login")
    public String login(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()){
            Optional<User> userOptional = userService.getUserByUsername(authentication.getName());
            if (userOptional.isPresent())
                return "redirect:/error/404";
        }

        model.addAttribute("newUser", new User());

        return "login";
    }

    @GetMapping(path = "/loginToAccess")
    public String loginToAccess(Model model) {
        return "redirect:/users/login";
    }

    @GetMapping(path = "/login/error")
    public String loginError(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("loginError","Incorrect username or password.");

        return "redirect:/users/login";
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> authenticate(@ModelAttribute User newUser,
                                               HttpServletRequest request) {
        AuthenticationResponse authenticationResponse = userService.authenticate(newUser);

        if (authenticationResponse.getAccessToken() == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/users/login/error");

            return new ResponseEntity<String>(headers, HttpStatus.FOUND);
        }

        return editSession(authenticationResponse, request);
    }

    @PostMapping(path = "/logout")
    public RedirectView logout(Model model,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        userService.logout(request, response);

        redirectAttributes.addFlashAttribute("info", "Logged out Successfully.");

        return new RedirectView("/index");
    }

    @GetMapping(path = "/register")
    public String register(Model model) {
        model.addAttribute("newUser", new User());

        return "register";
    }

    @GetMapping(path = "/register/error")
    public String registerError(RedirectAttributes redirectAttributes) {

        return "redirect:/users/register";
    }

    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(@ModelAttribute User newUser,
                                               @RequestParam("roleRadio") String roleName,
                                               HttpServletRequest request) {

        AuthenticationResponse authenticationResponse = userService.registerUser(newUser, roleName);

        if (authenticationResponse.getAccessToken() == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/users/register/error");
            if (authenticationResponse.getRefreshToken() == null)
                return new ResponseEntity<String>("null", headers, HttpStatus.FOUND);

            return new ResponseEntity<String>(authenticationResponse.getRefreshToken(), headers, HttpStatus.FOUND);
        }

        return editSession(authenticationResponse, request);
    }

    private ResponseEntity<String> editSession(AuthenticationResponse authenticationResponse,
                                               HttpServletRequest httpServletRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authenticationResponse.getAccessToken());
        headers.add("Location", "/index");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                ServletUriComponentsBuilder.fromServletMapping(httpServletRequest).path("/index").build().toUri(),
                HttpMethod.GET,
                entity,
                String.class);
    }
}
