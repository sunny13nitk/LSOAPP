package com.sap.cap.esmapi.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatgSrv;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.ui.pojos.TY_ESS_Stats;
import com.sap.cap.esmapi.ui.srv.intf.IF_ESS_UISrv;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cap.esmapi.utilities.srvCloudApi.srv.intf.IF_SrvCloudAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/esslocal")
public class ESSLocalController
{
    @Autowired
    private IF_UserAPISrv userSrv;

    @Autowired
    private IF_ESS_UISrv uiSrv;


    @Autowired
    private IF_SrvCloudAPI srvCloudApiSrv;

    @Autowired
    private MessageSource msgSrc;


    @Autowired
    private TY_CatgCus catgCusSrv;

    @Autowired
    private IF_CatgSrv catgTreeSrv;

  

    @GetMapping("/")
    public String showCasesList4User(Model model)
    {
        if( userSrv != null)
        {

             /*
                    //1 Populate User Details - Token Simulation locally for UI and logical Validation. 
             */
                 
                //Local Load for Testing
                 Ty_UserAccountContact userAcc = getUserAccount();

                 TY_UserESS userDetails = new TY_UserESS();
                 userDetails.setUserDetails(userAcc);
                 try 
                {
                    userDetails.setCases(srvCloudApiSrv.getCases4User(userAcc.getAccountId(), userAcc.getContactId()) );
                    if(userDetails != null && uiSrv != null)
                    {
                        TY_ESS_Stats stats = uiSrv.getStatsForUserCases(userDetails.getCases());
                        model.addAttribute("userInfo", userDetails);
                        model.addAttribute("stats", stats);
                    }
                } 
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                
               

                
            
        }

        return "essListView";
    }


    private Ty_UserAccountContact getUserAccount() 
    {
        return new Ty_UserAccountContact("I057386", "Sunny Bhardwaj", "sunny.bhardwaj@sap.com",
        "11eda929-5152-18be-afdb-81d9ac010a00", "11eda929-71b5-43ce-afdb-81d9ac010a00");

        //    return new Ty_UserAccountContact("Dummy", "ESS Test User", "test@gmail.com",
        //               null, null);
    }


    @GetMapping("/createCase/{caseType}")
	public String showTxnDetails4Scrip(@PathVariable("caseType") EnumCaseTypes caseType , Model model) throws Exception
	{
		
		final String viewName = "caseForm";
        String accountId;

		if (StringUtils.hasText(caseType.toString()) && userSrv != null)
		{
			System.out.println("Case Type Selected for Creation: " + caseType);

            TY_UserESS userDetails = new TY_UserESS();

            //1. Check if Account Exists for the logged in User as A/C is mandatory to create a case
           
            // --- FOR PROD
            // if(StringUtils.hasText(userSrv.getUserDetails4mSession().getAccountId()))
            // {
                   
            //     userDetails.setUserDetails(userSrv.getUserDetails4mSession());
            //     model.addAttribute("userInfo", userDetails);     
            //     accountId = userSrv.getUserDetails4mSession().getAccountId();
            // }
           
            // -- FOR TEST : STARTS
            if(StringUtils.hasText( getUserAccount().getAccountId()))
            {
                accountId = getUserAccount().getAccountId(); 
            }
            // -- FOR TEST : ENDS
            else //Create the Account with logged in User credentials
            {
                accountId = userSrv.createAccount(); //Implictly refreshed in buffer
            }

            //Prepare Case Model - Form
            if(StringUtils.hasText(accountId) && !CollectionUtils.isEmpty(catgCusSrv.getCustomizations()))
            {

                Optional<TY_CatgCusItem> cusItemO = catgCusSrv.getCustomizations().stream().filter(g->g.getCaseTypeEnum().toString().equals(caseType.toString())).findFirst();
                if(cusItemO.isPresent() && catgTreeSrv != null)
                {

                    //For TEST ONLY: Starts
                    userDetails.setUserDetails(getUserAccount());
                    model.addAttribute("userInfo", userDetails);
                    //For TEST ONLY: ENDS


                    model.addAttribute("caseTypeStr", caseType.toString());

                    TY_Case_Form caseForm = new TY_Case_Form();
                    caseForm.setAccId(accountId);   //hidden
                    caseForm.setCaseTxnType(cusItemO.get().getCaseType()); //hidden
                    model.addAttribute("caseForm", caseForm);

                    model.addAttribute("formError", null);

                    //also Upload the Catg. Tree as per Case Type
                    model.addAttribute("catgsList", catgTreeSrv.getCaseCatgTree4LoB(caseType).getCategories());

                    

                }

             
            }


		}
		
		return viewName;
	}



    @PostMapping("/saveCase")
	public String updatePFSchema(@ModelAttribute("caseForm") TY_Case_Form caseForm, Model model)
	{
        TY_UserESS userDetails = new TY_UserESS();
        if(caseForm != null)
        {
            System.out.println(caseForm.toString());

            //Validate Case Form
            List<String> msgs = validateCaseForm(caseForm);
            if(!CollectionUtils.isEmpty(msgs))
            {
                model.addAttribute("formError", msgs);
                /*
                 ------ Prepare Model for REload
                */
                // --- FOR PROD
                // if(StringUtils.hasText(userSrv.getUserDetails4mSession().getAccountId()))
                // {
                    
                //     userDetails.setUserDetails(userSrv.getUserDetails4mSession());
                //     model.addAttribute("userInfo", userDetails);     
                // }

                //For TEST ONLY: Starts
                userDetails.setUserDetails(getUserAccount());
                model.addAttribute("userInfo", userDetails);
                //For TEST ONLY: ENDS

                Optional<TY_CatgCusItem> cusItemO = catgCusSrv.getCustomizations().stream().filter(g->g.getCaseType().equals(caseForm.getCaseTxnType())).findFirst();
                if(cusItemO.isPresent() && catgTreeSrv != null)
                {
                    model.addAttribute("caseTypeStr", cusItemO.get().getCaseTypeEnum().toString());
                    model.addAttribute("caseForm", caseForm);   
                    //also Upload the Catg. Tree as per Case Type
                    model.addAttribute("catgsList", catgTreeSrv.getCaseCatgTree4LoB(cusItemO.get().getCaseTypeEnum()).getCategories());

                    return  "caseForm";
                }
            } //CaseForm has errors

            else //Commit the Case
            {
               //Create Notes if There is a description
            }

        }
        return "redirect:/esslocal/";
    }

    private List<String> validateCaseForm(TY_Case_Form caseForm)
    {
        List<String> msgs = new ArrayList<String>();

        if(!StringUtils.hasLength(caseForm.getAccId()))
        {
            msgs.add(msgSrc.getMessage("ERR_NO_AC", null, Locale.ENGLISH));
        }

        if(!StringUtils.hasLength(caseForm.getCaseTxnType()))
        {
            msgs.add(msgSrc.getMessage("ERR_NO_CASETYPE", null, Locale.ENGLISH));
        }

        if(!StringUtils.hasLength(caseForm.getCatgDesc()))
        {
            msgs.add(msgSrc.getMessage("ERR_NO_CATG", null, Locale.ENGLISH));
        }

        if(!StringUtils.hasLength(caseForm.getSubject()))
        {
            msgs.add(msgSrc.getMessage("ERR_NO_SUBJECT", null, Locale.ENGLISH));
        }
        
        return msgs;
    }

  
    
}
