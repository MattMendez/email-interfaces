package com.email.interfaces.controllers;

import com.email.interfaces.configuration.PubSubConfig;
import com.email.interfaces.dtos.LoginForm;
import com.email.interfaces.dtos.SignUpForm;
import com.email.interfaces.services.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.RedirectView;

import javax.servlet.http.HttpSession;

@RequestMapping("/interfaces")
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/index")
    public String startingPoint (HttpSession httpSession, Model model){
        model.addAttribute("loginForm", new LoginForm());
        httpSession.invalidate();
        return "index";
    }

    @PostMapping("/login")
    public String logInFromEmail(@ModelAttribute LoginForm loginForm, HttpSession httpSession, Model model) throws JsonProcessingException, InterruptedException {

        return loginService.verifyLoginForm(loginForm, httpSession, model);
    }

    @GetMapping("/new-user")
    public String newUser (Model model){
        model.addAttribute("signUpForm", new SignUpForm());

        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String newUserSignUp(@ModelAttribute SignUpForm signUpForm, HttpSession httpSession, Model model) throws JsonProcessingException, InterruptedException {

        return loginService.verifySignUpForm(signUpForm, httpSession, model);
    }

    @GetMapping("/logout")
    public String finishPoint (HttpSession httpSession,Model model){
        httpSession.invalidate();
        model.addAttribute("loginForm", new LoginForm());

        return "index";
    }

    @GetMapping("/regret")
    public String backToIndex (Model model){
        model.addAttribute("loginForm", new LoginForm());

        return "index";
    }
}
