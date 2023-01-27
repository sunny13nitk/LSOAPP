package com.sap.cap.esmapi.rest;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cap.esmapi.utilities.JSONUtility;
import com.sap.cap.esmapi.utilities.pojos.JSONAnotamy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class APIRestController 
{
    @Value("${caseurl}")
    private String caseUrl;
    @Value("${username}")
    private String userName;
    @Value("${password}")
    private String password;


    @GetMapping("/cases")
    private JsonNode getAllCases() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try 
        {
            if (StringUtils.hasLength(userName) && StringUtils.hasLength(password) && StringUtils.hasLength(caseUrl)) 
            {
                System.out.println("Url and Credentials Found!!");

                String encoding = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());

                HttpGet httpGet = new HttpGet(caseUrl);
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
                    HttpEntity entity = response.getEntity();
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


    @GetMapping("/casesAnotamy")
    private JSONAnotamy getAllCasesAnotamy() throws IOException
    {
        JSONAnotamy metaData = null;

        JsonNode jsonNode = getAllCases();
        if (jsonNode != null) 
        {

            metaData = new JSONAnotamy();

           // metaData.setAllKeys(JSONUtility.getKeysInJsonUsingJsonNodeFieldNames(json, mapper));

        }

        return metaData;

    }
}
