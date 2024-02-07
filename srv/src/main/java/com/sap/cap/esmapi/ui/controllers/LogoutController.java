package com.sap.cap.esmapi.ui.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController
{

    @GetMapping("/logout")
    private String showLogout()
    {
        return "logout";
    }

}
