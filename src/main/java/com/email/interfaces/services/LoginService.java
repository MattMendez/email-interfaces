package com.email.interfaces.services;

import com.email.interfaces.dtos.EmailListResponse;
import com.email.interfaces.dtos.LoginForm;
import com.email.interfaces.dtos.RequestVerification;
import com.email.interfaces.dtos.SignUpForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Service
public class LoginService {

    @Value("${users.microservice}")
    private String usersApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailService emailService;

    public String verifyLoginForm(LoginForm loginForm, HttpSession httpSession, Model model) throws JsonProcessingException, InterruptedException {

        if( loginForm.getEmail() != null
                && !loginForm.getEmail().isEmpty()
                &&  loginForm.getPassword() != null
                && !loginForm.getPassword().isEmpty())
        {

            if(!loginForm.getEmail().contains("@")){
                return "invalid-data";
            }

            System.out.println("Llamada a " + usersApiUrl + "/login-verify");
            RequestVerification requestVerification = restTemplate.postForObject(usersApiUrl + "/login-verify",loginForm, RequestVerification.class);

            if (requestVerification!= null && requestVerification.getExist()){
                httpSession.setAttribute("user", loginForm.getEmail());

                EmailListResponse emailListResponse = emailService.getAllEmails(loginForm.getEmail());

                model.addAttribute("emails",emailListResponse);

                return "home";
            } else {
                httpSession.setAttribute("user", loginForm.getEmail());

                return "login-fail";
            }

        } else {
            return "bad-credentials";
        }
    }


    public String verifySignUpForm(SignUpForm signUpForm, HttpSession httpSession, Model model) throws JsonProcessingException, InterruptedException {

        RequestVerification check = null;
        if( signUpForm.getEmail() != null
                && !signUpForm.getEmail().isEmpty()
                &&  signUpForm.getPassword() != null
                && !signUpForm.getPassword().isEmpty()
                && signUpForm.getEmail().contains("@"))
        {
            check  = restTemplate.postForObject(usersApiUrl + "/sign-up",signUpForm, RequestVerification.class);
        } else
        {
            return "invalid-data";
        }

        if(check.getExist()){
            httpSession.setAttribute("user",signUpForm.getEmail());

            EmailListResponse emailListResponse = emailService.getAllEmails(signUpForm.getEmail());

            model.addAttribute("emails",emailListResponse);

            return "home";
        }else {
            return "user-already-exist";
        }

    }
}
