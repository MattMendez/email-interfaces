package com.email.interfaces.controllers;

import com.email.interfaces.dtos.Email;
import com.email.interfaces.services.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.servlet.http.HttpSession;

@RequestMapping("/emails")
@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/new-email")
    public String newEmail(HttpSession httpSession, Model model){
        model.addAttribute("email", new Email());

        return "new-email";
    }

    @PostMapping("/new-email")
    public String saveNewEmail(@ModelAttribute Email email, HttpSession httpSession, Model model) throws JsonProcessingException, InterruptedException {
        email.setSender((String) httpSession.getAttribute("user"));

        return emailService.saveNewEmail(email, httpSession, model);
    }


}

