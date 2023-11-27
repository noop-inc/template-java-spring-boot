package dev.noop.blueprint.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/index")
    public String home(){
        return "index";
    }

    //    public String showRegistrationForm(Model model){
    // create model object to store form data
    @GetMapping("/register")
    public String register(){
        return "register";
    }
}