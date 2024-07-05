package com.example.ergasiapssd;

import com.example.ergasiapssd.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WebController {

    @RequestMapping("/")
    public String goToIndex(Model model) {
        return "index";
    }

    @RequestMapping("/index")
    public String redirectToIndex(Model model) {
        return goToIndex(model);
    }

    @RequestMapping("/error/404")
    public String p404(Model model) {
        return "error/404";
    }

    @RequestMapping(path = "/about")
    public String about() {
        return "about";
    }
}
