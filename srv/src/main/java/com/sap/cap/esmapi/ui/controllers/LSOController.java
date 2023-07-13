package com.sap.cap.esmapi.ui.controllers;

import java.util.Locale;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatalogSrv;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatgSrv;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserSessionSrv;
import com.sap.cds.services.request.UserInfo;
import com.sap.cloud.security.xsuaa.token.Token;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lso")
public class LSOController
{
    @Autowired
    private IF_UserSessionSrv userSessSrv;

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private TY_CatgCus catgCusSrv;

    @Autowired
    private IF_CatgSrv catgTreeSrv;

    @Autowired
    private IF_CatalogSrv catalogTreeSrv;

    @Autowired
    private MessageSource msgSrc;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private final String caseListVWRedirect = "redirect:/lso/";
    private final String caseFormErrorRedirect = "redirect:/lso/errForm/";
    private final String caseFormView = "caseFormLSO";
    private final String lsoCaseListView = "lsoCasesListView";

    @GetMapping("/")
    public String showCasesList(@AuthenticationPrincipal Token token, Model model)
    {

        if (token != null && userInfo != null && userSessSrv != null)
        {
            // Only Authenticated user via IDP
            if (userInfo.isAuthenticated())
            {

                // #AUTH checks to be done later after role collection(s) are published in
                // CL_UserSessionSrv
                TY_UserESS userDetails = new TY_UserESS();

                if (userSessSrv != null)
                {
                    // Get User Info. from XSUAA TOken
                    if (userSessSrv.getUserDetails(token) != null)
                        // check User and Account Bound
                        if (userSessSrv.getUserDetails4mSession() != null)
                        {
                            if (StringUtils.hasText(userSessSrv.getUserDetails4mSession().getAccountId())
                                    || StringUtils.hasText(userSessSrv.getUserDetails4mSession().getEmployeeId()))
                            {
                                if (!CollectionUtils.isEmpty(catgCusSrv.getCustomizations()))
                                {

                                    Optional<TY_CatgCusItem> cusItemO = catgCusSrv.getCustomizations().stream()
                                            .filter(g -> g.getCaseTypeEnum().toString()
                                                    .equals(EnumCaseTypes.Learning.toString()))
                                            .findFirst();
                                    if (cusItemO.isPresent() && catgTreeSrv != null)
                                    {
                                        userDetails.setUserDetails(userSessSrv.getUserDetails4mSession());
                                        userDetails.setCases(userSessSrv.getSessionInfo4Test().getCases());
                                        model.addAttribute("userInfo", userDetails);
                                        model.addAttribute("caseTypeStr", EnumCaseTypes.Learning.toString());
                                        // Rate Limit Simulation
                                        model.addAttribute("rateLimitBreached",
                                                userSessSrv.getCurrentRateLimitBreachedValue());

                                        // Even if No Cases - spl. for Newly Create Acc - to enable REfresh button
                                        model.addAttribute("sessMsgs", userSessSrv.getSessionMessages());

                                    }

                                    else
                                    {

                                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[]
                                        { EnumCaseTypes.Learning.toString() }, Locale.ENGLISH));
                                    }
                                }

                            }
                        }

                }
            }

        }

        return lsoCaseListView;

    }

}
