package com.tubes_impal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String welcome(Model model) {
        String message = "Welcome to EcoTrash Application!";
        model.addAttribute("message", message);
        return "general/index";
    }
}
