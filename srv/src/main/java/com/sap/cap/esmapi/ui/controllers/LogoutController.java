package com.sap.cap.esmapi.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/logout")
@Slf4j
public class LogoutController
{

    @GetMapping("/")
    public String showLogout()
    {
        log.info("Logout Trigerred....");
        return "logout";
    }

}
