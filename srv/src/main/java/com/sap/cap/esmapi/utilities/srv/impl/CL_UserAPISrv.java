package com.sap.cap.esmapi.utilities.srv.impl;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserAPISrv;
import com.sap.cloud.security.xsuaa.token.Token;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@Scope(value = "session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class CL_UserAPISrv implements IF_UserAPISrv
{
    private Ty_UserAccountContact userData;
    
    @Autowired
    private MessageSource msgSrc;

    @Autowired
    private TY_SrvCloudUrls srvCloudUrls;

    @Override
    public Ty_UserAccountContact getUserDetails(@AuthenticationPrincipal Token token) throws EX_ESMAPI 
    {
        if(token == null)
        {
            throw new EX_ESMAPI(msgSrc.getMessage("NO_TOKEN", null, Locale.ENGLISH));
        }

        else
        {
            //Return from Session if Populated else make some effort
            if(userData == null)
            {
                //Fetch and Return
                userData = new Ty_UserAccountContact();
                userData.setUserId(token.getLogonName());
                userData.setUserEmail(token.getEmail());

                return userData;
            }
        }
        return userData;
        
    }

    @Override
    public String getAccountIdByUserEmail(String userEmail) throws EX_ESMAPI 
    {
        String accountID = null;
        Map<String,String> accEmails = new HashMap<String,String>();
        if (StringUtils.hasText(userEmail) && srvCloudUrls != null)
        {
            if (StringUtils.hasText(srvCloudUrls.getAccountsUrl())) 
            {
                try 
                {
                    JsonNode accountsResp = getAllAccounts();
                    if (accountsResp != null)
                     {
                        JsonNode rootNode = accountsResp.path("value");
                        if (rootNode != null)
                        {
                            System.out.println("Accounts Bound!!");

                            Iterator<Map.Entry<String, JsonNode>> payloadItr = accountsResp.fields();
                            while (payloadItr.hasNext()) 
                            {
                                System.out.println("Payload Iterator Bound");
                                Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                String payloadFieldName = payloadEnt.getKey();
                                System.out.println("Payload Field Scanned:  " + payloadFieldName);

                                if (payloadFieldName.equals("value")) 
                                {
                                    Iterator<JsonNode> accItr = payloadEnt.getValue().elements();
                                    System.out.println("Accounts Iterator Bound");
                                    while (accItr.hasNext()) 
                                    {

                                        JsonNode accEnt = accItr.next();
                                        if (accEnt != null) 
                                        {
                                            String accid, accEmail;
                                            System.out.println("Account Entity Bound - Reading Account...");
                                            Iterator<String> fieldNames = accEnt.fieldNames();
                                            while (fieldNames.hasNext()) 
                                            {
                                                String accFieldName = fieldNames.next();
                                                System.out.println("Account Entity Field Scanned:  " + accFieldName);
                                                if (accFieldName.equals("id")) 
                                                {
                                                    System.out.println(
                                                            "Account Id Added : " + accEnt.get(accFieldName).asText());
                                                    accid = accEnt.get(accFieldName).asText();
                                                }

                                            }

                                        }

                                    }

                                }

                            }
                        }
                    }
                } catch (IOException e) {
                    throw new EX_ESMAPI(msgSrc.getMessage("API_AC_ERROR", new Object[] { e.getLocalizedMessage() },
                            Locale.ENGLISH));
                }
            }

        }
        return accountID;
    }

    private JsonNode getAllAccounts() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try 
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword()) && StringUtils.hasLength(srvCloudUrls.getAccountsUrl())) 
            {
                System.out.println("Url and Credentials Found!!");

                String encoding = Base64.getEncoder().encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                HttpGet httpGet = new HttpGet(srvCloudUrls.getAccountsUrl());
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpGet.addHeader("accept", "application/json");

                try 
                {
                    //Fire the Url
                    response = httpClient.execute(httpGet);

                    // verify the valid error code first
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK) 
                    {
                        throw new RuntimeException("Failed with HTTP error code : " + statusCode);
                    }

                    //Try and Get Entity from Response
                    org.apache.http.HttpEntity entity = response.getEntity();
                    String apiOutput = EntityUtils.toString(entity);
                    //Lets see what we got from API
                    System.out.println(apiOutput);

                    //Conerting to JSON
                    ObjectMapper mapper = new ObjectMapper();
                    jsonNode = mapper.readTree(apiOutput);
                    

                } catch (IOException e)
                {

                    e.printStackTrace();
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
