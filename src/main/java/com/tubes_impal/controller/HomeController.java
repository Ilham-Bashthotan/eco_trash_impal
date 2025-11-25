package com.tubes_impal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        String message = "Welcome to EcoTrash Application!";
        model.addAttribute("message", message);
        return "general/index";
    }
}
