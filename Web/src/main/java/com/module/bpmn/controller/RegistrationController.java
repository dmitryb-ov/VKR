package com.module.bpmn.controller;

import com.module.bpmn.model.User;
import com.module.bpmn.service.UserService;
import com.module.bpmn.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public RegistrationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String getPage(Authentication authentication, Model model){
        if(authentication != null){
            return "redirect:/";
        } else {
            model.addAttribute("user", new User());
            return "registration";
        }
    }

    @PostMapping("/signup")
    public String registration(@ModelAttribute("user") User user, BindingResult result, Model model){
        if(result.hasErrors()){
            return "registration";
        } else {
            userService.registrationUser(user);
            return "redirect:/login?success";
        }
    }
}
