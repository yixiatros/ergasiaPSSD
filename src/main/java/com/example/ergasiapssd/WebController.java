package com.example.ergasiapssd;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
