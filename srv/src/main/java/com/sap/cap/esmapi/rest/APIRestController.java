package com.sap.cap.esmapi.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cap.esmapi.utilities.pojos.JSONAnotamy;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cap.esmapi.utilities.srv.intf.IF_APISrv;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cds.services.request.UserInfo;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.sap.cloud.security.xsuaa.token.Token;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api")
public class APIRestController 
{

    @Autowired
    private IF_UserAPISrv userSrv;

    @Autowired
    private TY_SrvCloudUrls srvCloudUrls;

    @Autowired
    private IF_APISrv apiSrv;

    private final String equalsString = "=";

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

    

        

}
