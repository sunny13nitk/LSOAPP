package com.sap.cap.esmapi.ui.controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatalogSrv;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatgSrv;
import com.sap.cap.esmapi.events.event.EV_CaseFormSubmit;
import com.sap.cap.esmapi.events.event.EV_LogMessage;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_CaseFormAsync;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.enums.EnumMessageType;
import com.sap.cap.esmapi.utilities.enums.EnumStatus;
import com.sap.cap.esmapi.utilities.pojos.TY_Message;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContactEmployee;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserSessionSrv;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/poclocal")
@Slf4j
public class POCLocalController
{
    @Autowired
    private IF_UserSessionSrv userSessSrv;

    @Autowired
    private IF_UserAPISrv userSrv;

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

    private final String caseListVWRedirect = "redirect:/poclocal/";
    private final String caseFormErrorRedirect = "redirect:/poclocal/errForm";

    @GetMapping("/")
    public String showCasesList(Model model)
    {

        TY_UserESS userDetails = new TY_UserESS();

        // Mocking the authentication

        if (userSessSrv != null)
        {
            userSessSrv.loadUser4Test();
            // check User and Account Bound
            if (userSessSrv.getUserDetails4mSession() != null)
            {
                if (StringUtils.hasText(userSessSrv.getUserDetails4mSession().getAccountId())
                        || StringUtils.hasText(userSessSrv.getUserDetails4mSession().getEmployeeId()))
                {
                    if (!CollectionUtils.isEmpty(catgCusSrv.getCustomizations()))
                    {

                        Optional<TY_CatgCusItem> cusItemO = catgCusSrv.getCustomizations().stream()
                                .filter(g -> g.getCaseTypeEnum().toString().equals(EnumCaseTypes.Learning.toString()))
                                .findFirst();
                        if (cusItemO.isPresent() && catgTreeSrv != null)
                        {
                            userDetails.setUserDetails(userSessSrv.getUserDetails4mSession());
                            userDetails.setCases(userSessSrv.getSessionInfo4Test().getCases());
                            model.addAttribute("userInfo", userDetails);
                            model.addAttribute("caseTypeStr", EnumCaseTypes.Learning.toString());
                            // Rate Limit Simulation
                            model.addAttribute("rateLimitBreached", userSessSrv.getCurrentRateLimitBreachedValue());

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

        return "essListViewPOCLocal";

    }

    @GetMapping("/createCase/")
    public String showCaseAsyncForm(Model model)
    {
        String viewCaseForm = "caseFormPOCLocal";

        if ((StringUtils.hasText(userSessSrv.getUserDetails4mSession().getAccountId())
                || StringUtils.hasText(userSessSrv.getUserDetails4mSession().getEmployeeId()))
                && !CollectionUtils.isEmpty(catgCusSrv.getCustomizations()))
        {

            Optional<TY_CatgCusItem> cusItemO = catgCusSrv.getCustomizations().stream()
                    .filter(g -> g.getCaseTypeEnum().toString().equals(EnumCaseTypes.Learning.toString())).findFirst();
            if (cusItemO.isPresent() && catgTreeSrv != null)
            {

                model.addAttribute("caseTypeStr", EnumCaseTypes.Learning.toString());

                // Before case form Inititation we must check the Rate Limit for the Current
                // User Session --current Form Submission added for Rate Limit Evaulation
                if (userSessSrv.isWithinRateLimit())
                {
                    // Populate User Details
                    TY_UserESS userDetails = new TY_UserESS();
                    userDetails.setUserDetails(userSessSrv.getUserDetails4mSession());
                    model.addAttribute("userInfo", userDetails);

                    // Populate Case Form Details
                    TY_Case_Form caseForm = new TY_Case_Form();
                    if (userSessSrv.getUserDetails4mSession().isEmployee())
                    {
                        caseForm.setAccId(userSessSrv.getUserDetails4mSession().getAccountId()); // hidden
                    }
                    else
                    {
                        caseForm.setAccId(userSessSrv.getUserDetails4mSession().getEmployeeId()); // hidden
                    }

                    caseForm.setCaseTxnType(cusItemO.get().getCaseType()); // hidden
                    model.addAttribute("caseForm", caseForm);

                    model.addAttribute("formError", null);

                    // also Upload the Catg. Tree as per Case Type
                    model.addAttribute("catgsList",
                            catalogTreeSrv.getCaseCatgTree4LoB(EnumCaseTypes.Learning).getCategories());

                }
                else
                {
                    // Not Within Rate Limit - REdirect to List View
                    viewCaseForm = caseListVWRedirect;

                }

            }
            else
            {

                throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[]
                { EnumCaseTypes.Learning.toString() }, Locale.ENGLISH));
            }

        }

        return viewCaseForm;
    }

    @PostMapping("/saveCase")
    public String saveCase(@ModelAttribute("caseForm") TY_Case_Form caseForm, Model model)
    {

        String viewName = caseListVWRedirect;
        if (caseForm != null && userSessSrv != null)
        {
            if (userSessSrv.getUserDetails4mSession().isEmployee())
            {
                caseForm.setEmployee(true);
            }

            log.info("Processing of Case Form - UI layer :Begins....");

            // Any Validation Error(s) on the Form or Submission not possible
            if (!userSessSrv.SubmitCaseForm(caseForm))
            {
                // Redirect to Error Processing of Form
                viewName = caseFormErrorRedirect;
            }
            else
            {
                // Fire Case Submission Event - To be processed Asyncronously
                EV_CaseFormSubmit eventCaseSubmit = new EV_CaseFormSubmit(this,
                        userSessSrv.getCurrentForm4Submission());
                applicationEventPublisher.publishEvent(eventCaseSubmit);
            }

            log.info("Processing of Case Form - UI layer :Ends....");
        }

        return viewName;

    }

    private Ty_UserAccountContactEmployee getUserAccount()
    {
        return new Ty_UserAccountContactEmployee("I057386", "Sunny Bhardwaj", "sunny.bhardwaj@sap.com",
                "11eda929-5152-18be-afdb-81d9ac010a00", "11eda929-71b5-43ce-afdb-81d9ac010a00",
                "11ed17c5-47d5-c4de-afdb-818bd8010a00", false, false);

    }
}
