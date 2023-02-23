package com.sap.cap.esmapi.ui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.pojos.TY_CatgGuidsDesc;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatgSrv;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.ui.pojos.TY_ESS_Stats;
import com.sap.cap.esmapi.ui.srv.intf.IF_ESS_UISrv;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_Account_CaseCreate;
import com.sap.cap.esmapi.utilities.pojos.TY_Case_SrvCloud;
import com.sap.cap.esmapi.utilities.pojos.TY_CatgLvl1_CaseCreate;
import com.sap.cap.esmapi.utilities.pojos.TY_Description_CaseCreate;
import com.sap.cap.esmapi.utilities.pojos.TY_NotesCreate;
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
                 userSrv.setUserAccount(userAcc);
                 
                 TY_UserESS userDetails = new TY_UserESS();
                 userDetails.setUserDetails(userAcc);
                try 
                {
                   
                    
                    model.addAttribute("userInfo", userDetails);

                    //Set Cases to Null on each refresh
                    userDetails.setCases(null);
                    userDetails.setCases(srvCloudApiSrv.getCases4User(userAcc.getAccountId(), userAcc.getContactId()) );
                    if(userDetails != null && uiSrv != null && !CollectionUtils.isEmpty(userDetails.getCases()))
                    {
                        System.out.println("Number of cases post API call 4m Controller: " +userDetails.getCases().size() );
                        TY_ESS_Stats stats = uiSrv.getStatsForUserCases(userDetails.getCases());
        
                        model.addAttribute("stats", stats);
                        
                    }

                    //Even if No Cases - spl. for Newly Create Acc - to enable REfresh button
                    model.addAttribute( "sessMsgs" , userSrv.getSessionMessages()); 
                    
                } 
                catch (Exception e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASES_LIST", new Object[]{ e.getLocalizedMessage()}, Locale.ENGLISH));
                }

                
               

                
            
        }

        return "essListView";
    }


    private Ty_UserAccountContact getUserAccount() 
    {
        // return new Ty_UserAccountContact("I057386", "Sunny Bhardwaj", "sunny.bhardwaj@sap.com",
        // "11eda929-5152-18be-afdb-81d9ac010a00", "11eda929-71b5-43ce-afdb-81d9ac010a00");

         
        return new Ty_UserAccountContact("Dummy ESS2", "ESS Portal Test1", "testess2@gmail.com",
                       "11edb2b5-5f45-64fe-afdb-81df97010a00", null);

        
            // if(userSrv.getUserDetails4mSession() != null)
            // {
            //     System.out.println("Account from Session");
            //     return new Ty_UserAccountContact("Dummy ESS5", "ESS Portal Test5", "testess5@gmail.com",
            //            userSrv.getUserDetails4mSession().getAccountId(), userSrv.getUserDetails4mSession().getContactId());
               
            // }
            // else
            // {
            //     System.out.println("Account is New");
            //     return new Ty_UserAccountContact("Dummy ESS5", "ESS Portal Test5", "testess5@gmail.com",
            //            null, null);

                       
            // }           
    }


    @GetMapping("/createCase/{caseType}")
	public String showTxnDetails4Scrip(@PathVariable("caseType") EnumCaseTypes caseType , Model model) throws Exception
	{
		
		final String viewName = "caseForm";
        String accountId;

        try
        {

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
                    else
                    {
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[]{ caseType.toString()}, Locale.ENGLISH));
                    }

                
                }


            }

        }
        catch (Exception e)
        {
                 throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASES_FORM", new Object[]{ e.getLocalizedMessage()}, Locale.ENGLISH));
        }
		
		return viewName;
	}



    @PostMapping("/saveCase")
	public String saveCase(@ModelAttribute("caseForm") TY_Case_Form caseForm, Model model)
	{
        TY_UserESS userDetails = new TY_UserESS();
        TY_Case_SrvCloud newCaseEntity = new TY_Case_SrvCloud();
        Optional<TY_CatgCusItem> cusItemO = null;

        if(caseForm != null)
        {
            System.out.println(caseForm.toString());

            //Validate Case Form
            List<String> msgs = validateCaseForm(caseForm);
            cusItemO = catgCusSrv.getCustomizations().stream().filter(g->g.getCaseType().equals(caseForm.getCaseTxnType())).findFirst();
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


                newCaseEntity.setAccount(new TY_Account_CaseCreate(caseForm.getAccId())); //Account ID 
                newCaseEntity.setCaseType(caseForm.getCaseTxnType());  //Case Txn. Type
                newCaseEntity.setSubject(caseForm.getSubject()); // Subject

                //Fetch CatgGuid by description from Customizing - Set Categories
                if(cusItemO.isPresent() && catgTreeSrv != null)
                {
                    Optional<TY_CatgGuidsDesc> catgGuidDescO = catgTreeSrv.getCaseCatgTree4LoB(cusItemO.get().getCaseTypeEnum()).getCategories().stream()
                    .filter(h->h.getDescription().equals(caseForm.getCatgDesc())).findFirst();
                    if(catgGuidDescO.isPresent())
                    {
                        TY_CatgGuidsDesc catgGuidDesc = catgGuidDescO.get();
                        if(catgGuidDesc != null)
                        {
                            if(StringUtils.hasText(catgGuidDesc.getLvl1guid()))
                            {
                                newCaseEntity.setCategoryLevel1(new TY_CatgLvl1_CaseCreate(catgGuidDesc.getLvl1guid()));
                            }

                            if(StringUtils.hasText(catgGuidDesc.getLvl2guid()))
                            {
                                newCaseEntity.setCategoryLevel2(new TY_CatgLvl1_CaseCreate(catgGuidDesc.getLvl2guid()));
                            }

                        }
                    }
                }
                

               //Create Notes if There is a description
               if(StringUtils.hasText(caseForm.getDescription()))
               {
                    //Create Note and Get Guid back
                    String noteId = srvCloudApiSrv.createNotes(new TY_NotesCreate(caseForm.getDescription()));
                    if(StringUtils.hasText(noteId))
                    {
                        newCaseEntity.setDescription(new TY_Description_CaseCreate(noteId));
                    }
               }

               //Case Payload is now Ready:  Post and get the Case ID back
               try
               {
                        String caseID = srvCloudApiSrv.createCase(newCaseEntity);
                        if(StringUtils.hasText(caseID))
                        {
                                System.out.println("Case ID : " + caseID + " created..");
                                //Populate Success message in session
                                userSrv.addSessionMessage(msgSrc.getMessage("SUCC_CASE", new Object[]{caseID, cusItemO.get().getCaseTypeEnum().toString()}, Locale.ENGLISH));
                        }
               }
               catch(Exception e)
               {
                    throw new EX_ESMAPI( msgSrc.getMessage("ERR_CASE_POST", new Object[]{ e.getLocalizedMessage()}, Locale.ENGLISH));
               }
               
               
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
