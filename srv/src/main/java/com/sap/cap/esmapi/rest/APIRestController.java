package com.sap.cap.esmapi.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cap.esmapi.catg.pojos.TY_CaseCatgTree;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatgSrv;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.constants.GC_Constants;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.JSONAnotamy;
import com.sap.cap.esmapi.utilities.pojos.TY_AccountCreate;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseESS;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseGuidId;
import com.sap.cap.esmapi.utilities.pojos.TY_DefaultComm;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cap.esmapi.utilities.srv.intf.IF_APISrv;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cloud.security.xsuaa.token.Token;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class APIRestController 
{

    @Autowired
    private IF_UserAPISrv userSrv;

    @Autowired
    private TY_SrvCloudUrls srvCloudUrls;

    @Autowired
    private TY_CatgCus catgCus;

    @Autowired
    private IF_APISrv apiSrv;

    @Autowired
    private MessageSource msgSrc;

    @Autowired
    private IF_CatgSrv catgSrv;

    private final String equalsString = "=";


    @GetMapping("/caseIds")
    public List<TY_CaseGuidId> getCaseGuidIdList()
    {
        List<TY_CaseGuidId> casesGuidIdsList = null;

        try
        {
            
            JsonNode jsonNode = getAllCases();

            if(jsonNode != null)
            {

                JsonNode rootNode = jsonNode.path("value");
                if(rootNode != null)
                {
                    System.out.println("Cases Bound!!");
                    casesGuidIdsList = new ArrayList<TY_CaseGuidId>();
    
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
                                    String caseid = null, caseguid = null;
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


                                                                    
                                    }

                                    if(StringUtils.hasText(caseid) && StringUtils.hasText(caseguid))
                                    {
                                        casesGuidIdsList.add(new TY_CaseGuidId(caseguid, caseid));
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
 
               


        return casesGuidIdsList;
    }



    @GetMapping("/authInfo")
    public Map<String, String> sayHello(@AuthenticationPrincipal Token token)
    {

  
        Map<String, String> result = new HashMap<>();
        result.put("grant type", token.getGrantType());
        result.put("client id", token.getClientId());
        result.put("subaccount id", token.getSubaccountId());
        result.put("zone id", token.getZoneId());
        result.put("logon name", token.getLogonName());
        result.put("family name", token.getFamilyName());
        result.put("given name", token.getGivenName());
        result.put("email", token.getEmail());
        result.put("authorities", String.valueOf(token.getAuthorities()));
        result.put("scopes", String.valueOf(token.getScopes()));

        return result;
    }

    @GetMapping("/userInfo")
    public Ty_UserAccountContact getUserInfo(@AuthenticationPrincipal Token token)
    {
        return userSrv.getUserDetails(token);

    }

 
    @GetMapping("/casesCount")
    private String getNumberofCases() throws IOException
    {
        return String.valueOf(apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getCasesUrl()));
    }

    @GetMapping("/cases")
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
                    url = srvCloudUrls.getCasesUrl() + srvCloudUrls.getTopSuffix() + equalsString + numCases;

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


    @GetMapping("/casesESS")
    private List<TY_CaseESS> getAllCasesESS() throws IOException
    {
        List<TY_CaseESS> casesESSList = null;

        try
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

       catch (Exception e) 
        {
            e.printStackTrace();
        }
 
               


        return casesESSList;
    }

    
    @GetMapping("/ESS")
    private TY_UserESS getESSPortal(@AuthenticationPrincipal Token token)
    {
        TY_UserESS userDetails = userSrv.getESSDetails(token);

        return userDetails;
    }

   
   
    @GetMapping("/casesAnatomy")
    private JSONAnotamy getAllCasesAnotamy() throws IOException
    {
        JSONAnotamy metaData = null;

        List<String> caseIds = new ArrayList<String>();

        JsonNode jsonNode = getAllCases();
       

        if(jsonNode != null)
        {
            JsonNode rootNode = jsonNode.path("value");
            if(rootNode != null)
            {
                System.out.println("Cases Bound!!");

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
                                System.out.println("Cases Entity Bound - Reading Case...");
                                Iterator<String> fieldNames = caseEnt.fieldNames();
                                while (fieldNames.hasNext()) 
                                {
                                    String   caseFieldName  = fieldNames.next();
                                    System.out.println("Case Entity Field Scanned:  " + caseFieldName);
                                    if(caseFieldName.equals("id"))
                                    {
                                        System.out.println("Case Id Added : " + caseEnt.get(caseFieldName).asText());
                                        if(StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                        {
                                            caseIds.add(caseEnt.get(caseFieldName).asText());
                                        }
                                    }
                                                                
                                }

                            }
                       

                        }

                    }
                    
                    


                    
                    

                    
                    
                }
            }

            if(CollectionUtils.isNotEmpty(caseIds))
            {

               // List<String> distinctCaseIDs = caseIds.stream().distinct().collect(Collectors.toList());
                metaData = new JSONAnotamy();
                metaData.setCaseIDS(caseIds);
            }
        }
        
        return metaData;
    }


    @GetMapping("/accByEmail")
    private String getAccountIdByEmail(@RequestParam(name = "email", required = true) String email ) throws IOException
    {
        return userSrv.getAccountIdByUserEmail(email);
         
    }

    @GetMapping("/cpByEmail")
    private String getContactIdByEmail(@RequestParam(name = "email", required = true) String email ) throws IOException
    {
        return userSrv.getContactPersonIdByUserEmail(email);
         
    }


    @GetMapping("/cfg")
    private TY_CatgCus checkCaseCus()
    {
        return this.catgCus;
    }

    @GetMapping("/cfgCatg/{caseType}")
    private TY_CaseCatgTree checkCaseCusCatg(@PathVariable("caseType") EnumCaseTypes caseType)
    {
        return catgSrv.getCaseCatgTree4LoB(caseType);
    }

    @GetMapping("/accURL")
    private String getACCURL( )
    {
        String url = null;
        String requestBody = null;
        JsonNode jsonNode = null;
        String accountId = null;

        if(StringUtils.hasText(srvCloudUrls.getAccountsUrl()))
        {
            String[] urlParts = srvCloudUrls.getAccountsUrl().split("\\?");
            if(urlParts.length > 0)
            {
                url = urlParts[0];
            }
        }
        
        if(StringUtils.hasText(url))
        {
            TY_AccountCreate newAccount = new TY_AccountCreate
            ("Mohd. Shami", GC_Constants.gc_roleCustomer, GC_Constants.gc_statusACTIVE, new TY_DefaultComm("mohd.shami@gmail.com") );

            if(newAccount != null)
            {
                HttpClient httpclient = HttpClients.createDefault();
                
                
                    String encoding = Base64.getEncoder().encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                    httpPost.addHeader("Content-Type", "application/json");

                    ObjectMapper objMapper = new ObjectMapper();
                    try 
                    {

                        requestBody = objMapper.writeValueAsString(newAccount);
                        System.out.println(requestBody);

                        StringEntity entity = new StringEntity(requestBody,ContentType.APPLICATION_JSON);
                        httpPost.setEntity(entity);

                        //POST Account in Service Cloud
                        try 
                        {
                            // Fire the Url
                            HttpResponse response = httpclient.execute(httpPost);
                            // verify the valid error code first
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode != HttpStatus.SC_CREATED) 
                            {
                                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
                            }

                            // Try and Get Entity from Response
                            HttpEntity entityResp = response.getEntity();
                            String apiOutput = EntityUtils.toString(entityResp);
                            // Lets see what we got from API
                            System.out.println(apiOutput);

                            // Conerting to JSON
                            ObjectMapper mapper = new ObjectMapper();
                            jsonNode = mapper.readTree(apiOutput);


                            if(jsonNode != null)
                            {
                
                                JsonNode rootNode = jsonNode.path("value");
                                if(rootNode != null)
                                {
                                
                                    System.out.println("Account Bound!!");
                                    
                    
                                    Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                                    while (payloadItr.hasNext()) 
                                    {
                                        System.out.println("Payload Iterator Bound");
                                        Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                        String   payloadFieldName  = payloadEnt.getKey();
                                        System.out.println("Payload Field Scanned:  " + payloadFieldName);
                    
                                        if(payloadFieldName.equals("value"))
                                        {
                                            JsonNode accEnt = payloadEnt.getValue();
                                            System.out.println("New Account Entity Bound");
                                            if(accEnt != null)
                                                {
                                                    
                                                    System.out.println("Accounts Entity Bound - Reading Account...");
                                                    Iterator<String> fieldNames = accEnt.fieldNames();
                                                    while (fieldNames.hasNext()) 
                                                    {
                                                        String   accFieldName  = fieldNames.next();
                                                        System.out.println("Account Entity Field Scanned:  " + accFieldName);
                                                        if(accFieldName.equals("id"))
                                                        {
                                                            System.out.println("Account GUID Added : " + accEnt.get(accFieldName).asText());
                                                            if(StringUtils.hasText(accEnt.get(accFieldName).asText()))
                                                            {
                                                                accountId = accEnt.get(accFieldName).asText();
                                                            }
                                                        }
                                                        
                                                    }
                                                    
                                                }
                                                
                                            
                
                                        }							
                
                                    }			
                                }	
                            }		
                



                        } 
                        catch (IOException e)
                        {
                            throw new EX_ESMAPI(msgSrc.getMessage("ERR_ACC_POST", new Object[] { e.getLocalizedMessage() },
                                 Locale.ENGLISH));
                        }
                    }
                    catch (JsonProcessingException e) 
                    {
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_NEW_AC_JSON", new Object[] { e.getLocalizedMessage() },
                        Locale.ENGLISH));
                    }
                    
                   

            }  
        }



        return accountId;
    }
    

        

}
