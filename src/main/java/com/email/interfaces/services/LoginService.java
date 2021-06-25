package com.email.interfaces.services;

import com.email.interfaces.dtos.LoginForm;
import com.email.interfaces.dtos.RequestVerification;
import com.email.interfaces.dtos.SignUpForm;
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

    public String verifyLoginForm(LoginForm loginForm, HttpSession httpSession, Model model) {

        if( loginForm.getEmail() != null
                && !loginForm.getEmail().isEmpty()
                &&  loginForm.getPassword() != null
                && !loginForm.getPassword().isEmpty())
        {

            RequestVerification requestVerification = restTemplate.postForObject(usersApiUrl + "/login-verify",loginForm, RequestVerification.class);

            if (requestVerification!= null && requestVerification.getExist()){
                httpSession.setAttribute("user", loginForm.getEmail());
                //Faltan los mails

                return "home";
            } else {
                httpSession.setAttribute("user", loginForm.getEmail());

                return "login-fail";
            }

        } else {
            return "bad-credentials";
        }
    }


    public String verifySignUpForm(SignUpForm signUpForm, HttpSession httpSession, Model model) {

        RequestVerification check = new RequestVerification(false);
        if( signUpForm.getEmail() != null
                && !signUpForm.getEmail().isEmpty()
                &&  signUpForm.getPassword() != null
                && !signUpForm.getPassword().isEmpty())
        {
            check = restTemplate.postForObject(usersApiUrl + "/sign-up",signUpForm, RequestVerification.class);
        }

        if(check.getExist()){
            httpSession.setAttribute("user",signUpForm.getEmail());
            return "home";
        }else {
            return "user-already-exist";
        }

    }
}
