package com.sap.cap.esmapi.ui.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatgSrv;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.ui.pojos.TY_ESS_Stats;
import com.sap.cap.esmapi.ui.srv.intf.IF_ESS_UISrv;
import com.sap.cap.esmapi.utilities.constants.GC_Constants;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseESS;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cap.esmapi.utilities.srv.intf.IF_APISrv;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cds.services.request.UserInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private UserInfo userInfo;

    @Autowired
    private MessageSource msgSrc;

    @Autowired
    private IF_APISrv apiSrv;

    @Autowired
    private TY_CatgCus catgCusSrv;

    @Autowired
    private IF_CatgSrv catgTreeSrv;

    @Autowired
    private TY_SrvCloudUrls srvCloudUrls;

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
                    userDetails.setCases(getCases4User(userAcc.getAccountId(), userAcc.getContactId()) );
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



    private List<TY_CaseESS> getCases4User(String accountIdUser, String contactIdUser)throws IOException
    {
        List<TY_CaseESS> casesESSList = null;

        List<TY_CaseESS> casesESSList4User = null;

        try
        {
            if(accountIdUser == null)
            {
                return null;
            }
            else
            {
            JsonNode jsonNode = getAllCases();

            if(jsonNode != null)
            {

                JsonNode rootNode = jsonNode.path("value");
                if(rootNode != null)
                {
                    System.out.println("Cases Bound!!");
                    casesESSList = new ArrayList<TY_CaseESS>();
    
                    Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                    while (payloadItr.hasNext()) 
                    {
                        System.out.println("Payload Iterator Bound");
                        Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                        String   payloadFieldName  = payloadEnt.getKey();
                        System.out.println("Payload Field Scanned:  " + payloadFieldName);
    
                        if(payloadFieldName.equals("value"))
                        {
                            Iterator<JsonNode> casesItr = payloadEnt.getValue().elements();
                            System.out.println("Cases Iterator Bound");
                            while (casesItr.hasNext()) 
                            {
                                
                                JsonNode caseEnt = casesItr.next();
                                if(caseEnt != null)
                                {
                                    String caseid = null, caseguid = null, caseType = null, caseTypeDescription = null, subject= null, status= null,
                                    createdOn=null, accountId= null, contactId= null ;
                                    System.out.println("Cases Entity Bound - Reading Case...");
                                    Iterator<String> fieldNames = caseEnt.fieldNames();
                                    while (fieldNames.hasNext()) 
                                    {
                                        String   caseFieldName  = fieldNames.next();
                                        System.out.println("Case Entity Field Scanned:  " + caseFieldName);
                                        if(caseFieldName.equals("id"))
                                        {
                                            System.out.println("Case GUID Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                caseguid = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if(caseFieldName.equals("displayId"))
                                        {
                                            System.out.println("Case Id Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                caseid = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if(caseFieldName.equals("caseType"))
                                        {
                                            System.out.println("Case Type Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                caseType = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if(caseFieldName.equals("caseTypeDescription"))
                                        {
                                            System.out.println("Case Type Description Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                caseTypeDescription = caseEnt.get(caseFieldName).asText();
                                            }
                                        }


                                        if(caseFieldName.equals("subject"))
                                        {
                                            System.out.println("Case Subject Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                subject = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if(caseFieldName.equals("statusDescription"))
                                        {
                                            System.out.println("Case Status Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                status = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if(caseFieldName.equals("statusDescription"))
                                        {
                                            System.out.println("Case Status Added : " + caseEnt.get(caseFieldName).asText());
                                            if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                status = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if (caseFieldName.equals("adminData")) 
                                                {
                                                    System.out.println("Inside Admin Data:  " );

                                                    JsonNode admEnt = caseEnt.path("adminData");
                                                    if(admEnt != null)
                                                    {
                                                        System.out.println("AdminData Node Bound");

                                                        Iterator<String> fieldNamesAdm= admEnt.fieldNames();
                                                        while (fieldNamesAdm.hasNext()) 
                                                        {
                                                            String admFieldName = fieldNamesAdm.next();
                                                            if (admFieldName.equals("createdOn")) 
                                                                {
                                                                    System.out.println(
                                                                            "Created On : " + admEnt.get(admFieldName).asText());
                                                                    createdOn = admEnt.get(admFieldName).asText();
                                                                }
                                                        }

                                                    }
                                                }

                                        if (caseFieldName.equals("account")) 
                                                {
                                                    System.out.println("Inside Account:  " );

                                                    JsonNode accEnt = caseEnt.path("account");
                                                    if(accEnt != null)
                                                    {
                                                        System.out.println("Account Node Bound");

                                                        Iterator<String> fieldNamesAcc= accEnt.fieldNames();
                                                        while (fieldNamesAcc.hasNext()) 
                                                        {
                                                            String accFieldName = fieldNamesAcc.next();
                                                            if (accFieldName.equals("id")) 
                                                                {
                                                                    System.out.println(
                                                                            "Account ID : " + accEnt.get(accFieldName).asText());
                                                                    accountId = accEnt.get(accFieldName).asText();
                                                                }
                                                        }

                                                    }
                                                }        

                                        if (caseFieldName.equals("reporter")) 
                                                {
                                                    System.out.println("Inside Reporter:  " );

                                                    JsonNode repEnt = caseEnt.path("reporter");
                                                    if(repEnt != null)
                                                    {
                                                        System.out.println("Reporter Node Bound");

                                                        Iterator<String> fieldNamesRep= repEnt.fieldNames();
                                                        while (fieldNamesRep.hasNext()) 
                                                        {
                                                            String repFieldName = fieldNamesRep.next();
                                                            if (repFieldName.equals("id")) 
                                                                {
                                                                    System.out.println(
                                                                            "Reporter ID : " + repEnt.get(repFieldName).asText());
                                                                    contactId = repEnt.get(repFieldName).asText();
                                                                }
                                                        }

                                                    }
                                                }                

                                                                    
                                    }

                                    if(StringUtils.hasText(caseid) && StringUtils.hasText(caseguid))
                                    {
                                        if(StringUtils.hasText(createdOn))
                                        {
                                            // Parse the date-time string into OffsetDateTime
                                            OffsetDateTime odt = OffsetDateTime.parse(createdOn);
                                            // Convert OffsetDateTime into Instant
                                            Instant instant = odt.toInstant();
                                             // If at all, you need java.util.Date
                                            Date date = Date.from(instant);

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                                            String dateFormatted= sdf.format(date);
                                            
                                            casesESSList.add(new TY_CaseESS(caseguid, caseid, caseType, caseTypeDescription, subject, status, accountId, contactId, createdOn, date, dateFormatted));

                                        }
                                        else
                                        {
                                            casesESSList.add(new TY_CaseESS(caseguid, caseid, caseType, caseTypeDescription ,subject, status, accountId, contactId, createdOn, null,null));
                                        }
                                        
                                    }
    
                                }
                           
    
                            }
    
                        }
                                 
                    }
                }
                   
            }

        }

     }

       catch (Exception e) 
        {
            e.printStackTrace();
        }
        

       /*
         ------- FILTER FOR USER ACCOUNT or REPORTED BY CONTACT PERSON
       */

       if(! CollectionUtils.isEmpty(casesESSList))
       {
            casesESSList4User = casesESSList.stream().filter 
            (
                e->
                {

                    if(StringUtils.hasText(e.getContactId()))
                    {

                        if( e.getAccountId().equals(accountIdUser) 
                            ||
                            e.getContactId().equals(contactIdUser) )
                            {
                                return true;
                            }
                        
                    }
                    else
                    {
                        if( e.getAccountId().equals(accountIdUser) ) 
                        {
                            return true;
                        }

                    }
                    return false; 
                  
                       
    
                }
            ).collect(Collectors.toList());
            
       }
   


               


        return casesESSList4User;
    }



    private JsonNode getAllCases() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = null;

        try 
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword()) && StringUtils.hasLength(srvCloudUrls.getCasesUrl())) 
            {
                System.out.println("Url and Credentials Found!!");

                long numCases = apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getCasesUrl());
                if (numCases > 0)
                {
                    url = srvCloudUrls.getCasesUrl() + srvCloudUrls.getTopSuffix() + GC_Constants.equalsString + numCases;

                    String encoding = Base64.getEncoder()
                            .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                    httpGet.addHeader("accept", "application/json");

                    try 
                    {
                        // Fire the Url
                        response = httpClient.execute(httpGet);

                        // verify the valid error code first
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode != HttpStatus.SC_OK) 
                        {
                            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
                        }

                        // Try and Get Entity from Response
                        HttpEntity entity = response.getEntity();
                        String apiOutput = EntityUtils.toString(entity);
                        // Lets see what we got from API
                        System.out.println(apiOutput);

                        // Conerting to JSON
                        ObjectMapper mapper = new ObjectMapper();
                        jsonNode = mapper.readTree(apiOutput);

                    } 
                    catch (IOException e) 
                    {

                        e.printStackTrace();
                    }

                }

            }

        } 
        finally
        {
            httpClient.close();
        }
        return jsonNode;

        

    }

    

    
}
