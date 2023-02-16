package com.sap.cap.esmapi.ui.controllers;



import com.sap.cap.esmapi.ui.pojos.TY_ESS_Stats;
import com.sap.cap.esmapi.ui.srv.intf.IF_ESS_UISrv;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cds.services.request.UserInfo;
import com.sap.cloud.security.xsuaa.token.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ess")
public class ESSController 
{

    @Autowired
    private IF_UserAPISrv userSrv;

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private IF_ESS_UISrv uiSrv;

    @GetMapping("/")
    public String showCasesList4User(@AuthenticationPrincipal Token token, Model model)
    {
        if(token != null && userInfo != null && userSrv != null)
        {
            if(userInfo.isAuthenticated())
            {
                
                /*
                    //1. Get the Cases and Information for the user
                         -- This service call will implicitly create an Account for the User in Service Cloud if 
                         -- no Account could be found for the logon user by their Email Address derived from their token
                */
                TY_UserESS userDetails = userSrv.getESSDetails(token);
                if(userDetails != null && uiSrv != null)
                {
                    TY_ESS_Stats stats = uiSrv.getStatsForUserCases(userDetails.getCases());
                    model.addAttribute("userInfo", userDetails);
                    model.addAttribute("stats", stats);
                }
            }
        }

        return null;
    }
}
