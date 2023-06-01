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
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_CaseFormAsync;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/poclocal")
@Slf4j
public class POCLocalController
{

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

    @GetMapping("/")
    public String showCaseFormASynch(Model model)
    {

        final String viewName = "caseFormPOCLocal";
        String accountId = null;

        // Mocking the authentication

        // Local Load for Testing
        Ty_UserAccountContact userAcc = getUserAccount();
        userSrv.setUserAccount(userAcc);

        TY_UserESS userDetails = new TY_UserESS();
        userDetails.setUserDetails(userAcc);

        if (StringUtils.hasText(getUserAccount().getAccountId()))
        {
            accountId = getUserAccount().getAccountId();
        }

        if (StringUtils.hasText(accountId) && !CollectionUtils.isEmpty(catgCusSrv.getCustomizations()))
        {

            Optional<TY_CatgCusItem> cusItemO = catgCusSrv.getCustomizations().stream()
                    .filter(g -> g.getCaseTypeEnum().toString().equals(EnumCaseTypes.Learning.toString())).findFirst();
            if (cusItemO.isPresent() && catgTreeSrv != null)
            {

                // For TEST ONLY: Starts
                userDetails.setUserDetails(getUserAccount());
                model.addAttribute("userInfo", userDetails);
                // For TEST ONLY: ENDS

                model.addAttribute("caseTypeStr", EnumCaseTypes.Learning.toString());

                TY_Case_Form caseForm = new TY_Case_Form();
                caseForm.setAccId(accountId); // hidden
                caseForm.setCaseTxnType(cusItemO.get().getCaseType()); // hidden
                model.addAttribute("caseForm", caseForm);

                model.addAttribute("formError", null);

                // also Upload the Catg. Tree as per Case Type
                model.addAttribute("catgsList",
                        catalogTreeSrv.getCaseCatgTree4LoB(EnumCaseTypes.Learning).getCategories());

            }
            else
            {
                throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[]
                { EnumCaseTypes.Learning.toString() }, Locale.ENGLISH));
            }

        }
        return viewName;

    }

    @PostMapping("/saveCase")
    public String saveCase(@ModelAttribute("caseForm") TY_Case_Form caseForm, Model model)
    {

        if (caseForm != null && userSrv != null)
        {
            // Perform Validation(s) or Send errors

            // Valid Payload - log the details and fire the Event
            TY_CaseFormAsync payload = new TY_CaseFormAsync();
            Ty_UserAccountContact userdata = getUserAccount();
            if (userdata != null)
            {
                if (StringUtils.hasText(userdata.getUserEmail()))
                {
                    payload.setAuthenticated(true);
                    payload.setEmailId(userdata.getUserEmail());
                    payload.setSubmGuid(UUID.randomUUID().toString());
                    payload.setTimestamp(Timestamp.from(Instant.now()));
                    payload.setCaseForm(caseForm);

                    log.info("Case Creation Request with ID: " + payload.getSubmGuid() + " submitted..");

                    // Instantiate and Fire the Event
                    EV_CaseFormSubmit eventCaseSubmit = new EV_CaseFormSubmit(this, payload);
                    applicationEventPublisher.publishEvent(eventCaseSubmit);

                    // Populate Success message in session
                    userSrv.addSessionMessage(msgSrc.getMessage("SUCC_CASE_SUBM", new Object[]
                    { payload.getSubmGuid(), caseForm.getCaseTxnType() }, Locale.ENGLISH));

                    log.info("Case Form Submit completed.... " );
                }

            }

        }
        return "redirect:/poclocal/";
    }

    private Ty_UserAccountContact getUserAccount()
    {
        return new Ty_UserAccountContact("I057386", "Sunny Bhardwaj", "sunny.bhardwaj@sap.com",
                "11eda929-5152-18be-afdb-81d9ac010a00", "11eda929-71b5-43ce-afdb-81d9ac010a00");

    }
}
