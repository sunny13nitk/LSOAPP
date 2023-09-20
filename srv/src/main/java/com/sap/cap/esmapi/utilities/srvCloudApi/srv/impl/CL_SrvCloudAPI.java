package com.sap.cap.esmapi.utilities.srvCloudApi.srv.impl;

import java.io.IOException;
import java.net.IDN;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cap.esmapi.catg.pojos.TY_CatalogItem;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.status.pojos.TY_StatusCfgItem;
import com.sap.cap.esmapi.ui.pojos.TY_Attachment;
import com.sap.cap.esmapi.utilities.StringsUtility;
import com.sap.cap.esmapi.utilities.constants.GC_Constants;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_AccountCreate;
import com.sap.cap.esmapi.utilities.pojos.TY_AttachmentResponse;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseCatalogCustomizing;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseDetails;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseESS;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseGuidId;
import com.sap.cap.esmapi.utilities.pojos.TY_CasePatchInfo;
import com.sap.cap.esmapi.utilities.pojos.TY_Case_SrvCloud;
import com.sap.cap.esmapi.utilities.pojos.TY_Case_SrvCloud_Reply;
import com.sap.cap.esmapi.utilities.pojos.TY_DefaultComm;
import com.sap.cap.esmapi.utilities.pojos.TY_NotesCreate;
import com.sap.cap.esmapi.utilities.pojos.TY_NotesDetails;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContactEmployee;
import com.sap.cap.esmapi.utilities.srv.intf.IF_APISrv;
import com.sap.cap.esmapi.utilities.srvCloudApi.srv.intf.IF_SrvCloudAPI;
import com.sap.cap.esmapi.vhelps.pojos.TY_KeyValue;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CL_SrvCloudAPI implements IF_SrvCloudAPI
{

    @Autowired
    private TY_SrvCloudUrls srvCloudUrls;

    @Autowired
    private IF_APISrv apiSrv;

    @Autowired
    private TY_CatgCus caseTypeCus;

    @Autowired
    private MessageSource msgSrc;

    @Override
    public JsonNode getAllCases() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = null;

        try
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword())
                    && StringUtils.hasLength(srvCloudUrls.getCasesUrl()))
            {
                System.out.println("Url and Credentials Found!!");

                long numCases = apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getCasesUrl());
                if (numCases > 0)
                {
                    url = srvCloudUrls.getCasesUrl() + srvCloudUrls.getTopSuffix() + GC_Constants.equalsString
                            + numCases;

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
                        // System.out.println(apiOutput);

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

    @Override
    public List<TY_CaseESS> getCases4User(String accountIdUser, String contactIdUser) throws IOException
    {
        List<TY_CaseESS> casesESSList = null;

        List<TY_CaseESS> casesESSList4User = null;

        try
        {
            if (accountIdUser == null)
            {
                return null;
            }
            else
            {
                JsonNode jsonNode = getAllCases();

                if (jsonNode != null)
                {

                    JsonNode rootNode = jsonNode.path("value");
                    if (rootNode != null)
                    {
                        System.out.println("Cases Bound!!");
                        casesESSList = new ArrayList<TY_CaseESS>();

                        Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                        while (payloadItr.hasNext())
                        {
                            // System.out.println("Payload Iterator Bound");
                            Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                            String payloadFieldName = payloadEnt.getKey();
                            // System.out.println("Payload Field Scanned: " + payloadFieldName);

                            if (payloadFieldName.equals("value"))
                            {
                                Iterator<JsonNode> casesItr = payloadEnt.getValue().elements();
                                // System.out.println("Cases Iterator Bound");
                                while (casesItr.hasNext())
                                {

                                    JsonNode caseEnt = casesItr.next();
                                    if (caseEnt != null)
                                    {
                                        String caseid = null, caseguid = null, caseType = null,
                                                caseTypeDescription = null, subject = null, status = null,
                                                createdOn = null, accountId = null, contactId = null, origin = null;
                                        // System.out.println("Cases Entity Bound - Reading Case...");
                                        Iterator<String> fieldNames = caseEnt.fieldNames();
                                        while (fieldNames.hasNext())
                                        {
                                            String caseFieldName = fieldNames.next();
                                            // System.out.println("Case Entity Field Scanned: " + caseFieldName);
                                            if (caseFieldName.equals("id"))
                                            {
                                                // System.out.println("Case GUID Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseguid = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("displayId"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseid = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("caseType"))
                                            {
                                                // System.out.println("Case Type Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseType = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("caseTypeDescription"))
                                            {
                                                // System.out.println("Case Type Description Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseTypeDescription = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("subject"))
                                            {
                                                // System.out.println("Case Subject Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    subject = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("statusDescription"))
                                            {
                                                // System.out.println("Case Status Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    status = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("origin"))
                                            {
                                                // System.out.println("Case Status Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    origin = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("statusDescription"))
                                            {
                                                // System.out.println("Case Status Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    status = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("adminData"))
                                            {
                                                // System.out.println("Inside Admin Data: " );

                                                JsonNode admEnt = caseEnt.path("adminData");
                                                if (admEnt != null)
                                                {
                                                    // System.out.println("AdminData Node Bound");

                                                    Iterator<String> fieldNamesAdm = admEnt.fieldNames();
                                                    while (fieldNamesAdm.hasNext())
                                                    {
                                                        String admFieldName = fieldNamesAdm.next();
                                                        if (admFieldName.equals("createdOn"))
                                                        {
                                                            // System.out.println( "Created On : " +
                                                            // admEnt.get(admFieldName).asText());
                                                            createdOn = admEnt.get(admFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                            if (caseFieldName.equals("account"))
                                            {
                                                // System.out.println("Inside Account: " );

                                                JsonNode accEnt = caseEnt.path("account");
                                                if (accEnt != null)
                                                {
                                                    // System.out.println("Account Node Bound");

                                                    Iterator<String> fieldNamesAcc = accEnt.fieldNames();
                                                    while (fieldNamesAcc.hasNext())
                                                    {
                                                        String accFieldName = fieldNamesAcc.next();
                                                        if (accFieldName.equals("id"))
                                                        {
                                                            // System.out.println(
                                                            // "Account ID : " + accEnt.get(accFieldName).asText());
                                                            accountId = accEnt.get(accFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                            if (caseFieldName.equals("individualCustomer")
                                                    && (!StringUtils.hasText(accountId)))
                                            {
                                                // System.out.println("Inside Account: " );

                                                JsonNode accEnt = caseEnt.path("individualCustomer");
                                                if (accEnt != null)
                                                {
                                                    // System.out.println("Account Node Bound");

                                                    Iterator<String> fieldNamesAcc = accEnt.fieldNames();
                                                    while (fieldNamesAcc.hasNext())
                                                    {
                                                        String accFieldName = fieldNamesAcc.next();
                                                        if (accFieldName.equals("id"))
                                                        {
                                                            // System.out.println(
                                                            // "Account ID : " + accEnt.get(accFieldName).asText());
                                                            accountId = accEnt.get(accFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                            if (caseFieldName.equals("reporter"))
                                            {
                                                // System.out.println("Inside Reporter: " );

                                                JsonNode repEnt = caseEnt.path("reporter");
                                                if (repEnt != null)
                                                {
                                                    // System.out.println("Reporter Node Bound");

                                                    Iterator<String> fieldNamesRep = repEnt.fieldNames();
                                                    while (fieldNamesRep.hasNext())
                                                    {
                                                        String repFieldName = fieldNamesRep.next();
                                                        if (repFieldName.equals("id"))
                                                        {
                                                            // System.out.println(
                                                            // "Reporter ID : " + repEnt.get(repFieldName).asText());
                                                            contactId = repEnt.get(repFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                        }

                                        if (StringUtils.hasText(caseid) && StringUtils.hasText(caseguid))
                                        {
                                            if (StringUtils.hasText(createdOn))
                                            {
                                                // Parse the date-time string into OffsetDateTime
                                                OffsetDateTime odt = OffsetDateTime.parse(createdOn);
                                                // Convert OffsetDateTime into Instant
                                                Instant instant = odt.toInstant();
                                                // If at all, you need java.util.Date
                                                Date date = Date.from(instant);

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                                                String dateFormatted = sdf.format(date);

                                                casesESSList.add(new TY_CaseESS(caseguid, caseid, caseType,
                                                        caseTypeDescription, subject, status, accountId, contactId,
                                                        createdOn, date, dateFormatted, origin));

                                            }
                                            else
                                            {
                                                casesESSList.add(new TY_CaseESS(caseguid, caseid, caseType,
                                                        caseTypeDescription, subject, status, accountId, contactId,
                                                        createdOn, null, null, origin));
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
         * ------- FILTER FOR USER ACCOUNT or REPORTED BY CONTACT PERSON
         */

        if (!CollectionUtils.isEmpty(casesESSList))
        {
            casesESSList4User = casesESSList.stream().filter(e ->
            {
                // #ESMModule
                // If no Account Itself in Present in Case - Ignore Such Cases --Add Employee
                // with an and condition once ESM module is enabled
                if (!StringUtils.hasText(e.getAccountId()))
                {
                    return false;
                }

                if (StringUtils.hasText(e.getContactId()))
                {

                    if (e.getAccountId().equals(accountIdUser) || e.getContactId().equals(contactIdUser))
                    {
                        return true;
                    }

                }
                else
                {
                    if (e.getAccountId().equals(accountIdUser))
                    {
                        return true;
                    }

                }
                return false;

            }).collect(Collectors.toList());

        }

        if (!CollectionUtils.isEmpty(casesESSList4User))
        {
            System.out.println("# Cases returned in call : " + casesESSList4User.size());
        }
        return casesESSList4User;
    }

    @Override
    public List<TY_CaseGuidId> getCaseGuidIdList()
    {
        List<TY_CaseGuidId> casesGuidIdsList = null;

        try
        {

            JsonNode jsonNode = getAllCases();

            if (jsonNode != null)
            {

                JsonNode rootNode = jsonNode.path("value");
                if (rootNode != null)
                {
                    System.out.println("Cases Bound!!");
                    casesGuidIdsList = new ArrayList<TY_CaseGuidId>();

                    Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                    while (payloadItr.hasNext())
                    {
                        System.out.println("Payload Iterator Bound");
                        Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                        String payloadFieldName = payloadEnt.getKey();
                        System.out.println("Payload Field Scanned:  " + payloadFieldName);

                        if (payloadFieldName.equals("value"))
                        {
                            Iterator<JsonNode> casesItr = payloadEnt.getValue().elements();
                            System.out.println("Cases Iterator Bound");
                            while (casesItr.hasNext())
                            {

                                JsonNode caseEnt = casesItr.next();
                                if (caseEnt != null)
                                {
                                    String caseid = null, caseguid = null;
                                    System.out.println("Cases Entity Bound - Reading Case...");
                                    Iterator<String> fieldNames = caseEnt.fieldNames();
                                    while (fieldNames.hasNext())
                                    {
                                        String caseFieldName = fieldNames.next();
                                        System.out.println("Case Entity Field Scanned:  " + caseFieldName);
                                        if (caseFieldName.equals("id"))
                                        {
                                            System.out.println(
                                                    "Case GUID Added : " + caseEnt.get(caseFieldName).asText());
                                            if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                caseguid = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                        if (caseFieldName.equals("displayId"))
                                        {
                                            System.out
                                                    .println("Case Id Added : " + caseEnt.get(caseFieldName).asText());
                                            if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                            {
                                                caseid = caseEnt.get(caseFieldName).asText();
                                            }
                                        }

                                    }

                                    if (StringUtils.hasText(caseid) && StringUtils.hasText(caseguid))
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

    @Override
    public Long getNumberofCases() throws IOException
    {
        return apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getCasesUrl());
    }

    @Override
    public String getAccountIdByUserEmail(String userEmail) throws EX_ESMAPI
    {
        String accountID = null;
        Map<String, String> accEmails = new HashMap<String, String>();
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
                                            String accid = null, accEmail = null;
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

                                                if (accFieldName.equals("defaultCommunication"))
                                                {
                                                    System.out.println("Inside Default Communication:  ");

                                                    JsonNode commEnt = accEnt.path("defaultCommunication");
                                                    if (commEnt != null)
                                                    {
                                                        System.out.println("Comm's Node Bound");

                                                        Iterator<String> fieldNamesComm = commEnt.fieldNames();
                                                        while (fieldNamesComm.hasNext())
                                                        {
                                                            String commFieldName = fieldNamesComm.next();
                                                            if (commFieldName.equals("eMail"))
                                                            {
                                                                System.out.println("Account Email Added : "
                                                                        + commEnt.get(commFieldName).asText());
                                                                accEmail = commEnt.get(commFieldName).asText();
                                                            }
                                                        }

                                                    }
                                                }

                                            }
                                            // avoid null email accounts
                                            if (StringUtils.hasText(accid) && StringUtils.hasText(accEmail))
                                            {
                                                accEmails.put(accid, accEmail);
                                            }

                                        }

                                    }

                                }

                            }

                            // Filter by Email
                            Optional<Map.Entry<String, String>> OptionalAcc = accEmails.entrySet().stream()
                                    .filter(u -> u.getValue().equals(userEmail)).findFirst();
                            if (OptionalAcc.isPresent())
                            {
                                Map.Entry<String, String> account = OptionalAcc.get();
                                accountID = account.getKey(); // Return Account ID
                            }

                        }
                    }
                }
                catch (IOException e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("API_AC_ERROR", new Object[]
                    { e.getLocalizedMessage() }, Locale.ENGLISH));
                }
            }

        }
        return accountID;
    }

    @Override
    public JsonNode getAllAccounts() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = null;

        try
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword())
                    && StringUtils.hasLength(srvCloudUrls.getAccountsUrl()))
            {
                System.out.println("Url and Credentials Found!!");

                long numAccounts = apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getAccountsUrl());
                if (numAccounts > 0)
                {
                    url = srvCloudUrls.getAccountsUrl() + srvCloudUrls.getTopSuffix() + GC_Constants.equalsString
                            + numAccounts;
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
                        org.apache.http.HttpEntity entity = response.getEntity();
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

    @Override
    public JsonNode getAllEmployees() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = null;

        try
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword())
                    && StringUtils.hasLength(srvCloudUrls.getEmplUrl()))
            {
                System.out.println("Url and Credentials Found!!");

                long numEmpl = apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getEmplUrl());
                if (numEmpl > 0)
                {
                    url = srvCloudUrls.getEmplUrl() + srvCloudUrls.getTopSuffix() + GC_Constants.equalsString + numEmpl;
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
                            throw new RuntimeException(
                                    "Failed with HTTP error code : " + statusCode + "on Employees Read API");
                        }

                        // Try and Get Entity from Response
                        org.apache.http.HttpEntity entity = response.getEntity();
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

    @Override
    public JsonNode getAllContacts() throws IOException
    {
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = null;

        try
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword())
                    && StringUtils.hasLength(srvCloudUrls.getCpUrl()))
            {
                System.out.println("Url and Credentials Found!!");

                long numAccounts = apiSrv.getNumberofEntitiesByUrl(srvCloudUrls.getCpUrl());
                if (numAccounts > 0)
                {
                    url = srvCloudUrls.getCpUrl() + srvCloudUrls.getTopSuffix() + GC_Constants.equalsString
                            + numAccounts;
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
                        org.apache.http.HttpEntity entity = response.getEntity();
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

    @Override
    public String getContactPersonIdByUserEmail(String userEmail) throws EX_ESMAPI
    {
        String accountID = null;
        Map<String, String> accEmails = new HashMap<String, String>();
        if (StringUtils.hasText(userEmail) && srvCloudUrls != null)
        {
            if (StringUtils.hasText(srvCloudUrls.getCpUrl()))
            {
                try
                {
                    JsonNode accountsResp = getAllContacts();
                    if (accountsResp != null)
                    {
                        JsonNode rootNode = accountsResp.path("value");
                        if (rootNode != null)
                        {
                            System.out.println("Contacts Bound!!");

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
                                    System.out.println("Contacts Iterator Bound");
                                    while (accItr.hasNext())
                                    {

                                        JsonNode accEnt = accItr.next();
                                        if (accEnt != null)
                                        {
                                            String accid = null, accEmail = null;
                                            System.out.println("Contact Entity Bound - Reading Contact...");
                                            Iterator<String> fieldNames = accEnt.fieldNames();
                                            while (fieldNames.hasNext())
                                            {
                                                String accFieldName = fieldNames.next();
                                                System.out.println("Contact Entity Field Scanned:  " + accFieldName);
                                                if (accFieldName.equals("id"))
                                                {
                                                    System.out.println(
                                                            "Account Id Added : " + accEnt.get(accFieldName).asText());
                                                    accid = accEnt.get(accFieldName).asText();
                                                }

                                                if (accFieldName.equals("eMail"))
                                                {
                                                    System.out.println("Account Email Added : "
                                                            + accEnt.get(accFieldName).asText());
                                                    accEmail = accEnt.get(accFieldName).asText();
                                                }

                                            }
                                            // avoid null email accounts
                                            if (StringUtils.hasText(accid) && StringUtils.hasText(accEmail))
                                            {
                                                accEmails.put(accid, accEmail);
                                            }

                                        }

                                    }

                                }

                            }

                            // Filter by Email
                            Optional<Map.Entry<String, String>> OptionalAcc = accEmails.entrySet().stream()
                                    .filter(u -> u.getValue().equals(userEmail)).findFirst();
                            if (OptionalAcc.isPresent())
                            {
                                Map.Entry<String, String> account = OptionalAcc.get();
                                accountID = account.getKey(); // Return Account ID
                            }

                        }
                    }
                }
                catch (IOException e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("API_AC_ERROR", new Object[]
                    { e.getLocalizedMessage() }, Locale.ENGLISH));
                }
            }

        }
        return accountID;
    }

    @Override
    public String createAccount(String userEmail, String userName) throws EX_ESMAPI
    {
        String accountId = null;
        // User Email and UserName Bound
        if (StringUtils.hasText(userEmail) && StringUtils.hasText(userName))
        {
            TY_AccountCreate newAccount = new TY_AccountCreate(userName, GC_Constants.gc_roleCustomer,
                    GC_Constants.gc_statusACTIVE, true, new TY_DefaultComm(userEmail));

            if (newAccount != null)
            {
                HttpClient httpclient = HttpClients.createDefault();
                String accPOSTURL = getPOSTURL4BaseUrl(srvCloudUrls.getAccountsUrl());
                if (StringUtils.hasText(accPOSTURL))
                {
                    String encoding = Base64.getEncoder()
                            .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());
                    HttpPost httpPost = new HttpPost(accPOSTURL);
                    httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                    httpPost.addHeader("Content-Type", "application/json");

                    ObjectMapper objMapper = new ObjectMapper();
                    try
                    {
                        String requestBody = objMapper.writeValueAsString(newAccount);
                        System.out.println(requestBody);

                        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                        httpPost.setEntity(entity);

                        // POST Account in Service Cloud
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

                            // Conerting to JSON
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(apiOutput);

                            if (jsonNode != null)
                            {

                                JsonNode rootNode = jsonNode.path("value");
                                if (rootNode != null)
                                {

                                    System.out.println("Account Bound!!");

                                    Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                                    while (payloadItr.hasNext())
                                    {
                                        System.out.println("Payload Iterator Bound");
                                        Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                        String payloadFieldName = payloadEnt.getKey();
                                        System.out.println("Payload Field Scanned:  " + payloadFieldName);

                                        if (payloadFieldName.equals("value"))
                                        {
                                            JsonNode accEnt = payloadEnt.getValue();
                                            System.out.println("New Account Entity Bound");
                                            if (accEnt != null)
                                            {

                                                System.out.println("Accounts Entity Bound - Reading Account...");
                                                Iterator<String> fieldNames = accEnt.fieldNames();
                                                while (fieldNames.hasNext())
                                                {
                                                    String accFieldName = fieldNames.next();
                                                    System.out
                                                            .println("Account Entity Field Scanned:  " + accFieldName);
                                                    if (accFieldName.equals("id"))
                                                    {
                                                        System.out.println("Account GUID Added : "
                                                                + accEnt.get(accFieldName).asText());
                                                        if (StringUtils.hasText(accEnt.get(accFieldName).asText()))
                                                        {
                                                            accountId = accEnt.get(accFieldName).asText();

                                                        }
                                                        break;
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
                            throw new EX_ESMAPI(msgSrc.getMessage("ERR_ACC_POST", new Object[]
                            { e.getLocalizedMessage() }, Locale.ENGLISH));
                        }
                    }
                    catch (JsonProcessingException e)
                    {
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_NEW_AC_JSON", new Object[]
                        { e.getLocalizedMessage() }, Locale.ENGLISH));
                    }

                }

            }
        }
        return accountId;
    }

    @Override
    public TY_CaseCatalogCustomizing getActiveCaseTemplateConfig4CaseType(String caseType) throws EX_ESMAPI, IOException
    {
        TY_CaseCatalogCustomizing caseCus = null;
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String url = null;

        try
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword())
                    && StringUtils.hasLength(srvCloudUrls.getCaseTemplateUrl()))
            {
                System.out.println("Url and Credentials Found!!");

                url = srvCloudUrls.getCaseTemplateUrl() + caseType;

                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpGet.addHeader("accept", "application/json");

                // Fire the Url
                response = httpClient.execute(httpGet);

                // verify the valid error code first
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK)
                {

                    if (statusCode == HttpStatus.SC_NOT_FOUND)
                    {
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[]
                        { caseType }, Locale.ENGLISH));
                    }
                    else
                    {
                        throw new RuntimeException("Failed with HTTP error code : " + statusCode);
                    }

                }

                // Try and Get Entity from Response
                HttpEntity entity = response.getEntity();
                String apiOutput = EntityUtils.toString(entity);
                // Lets see what we got from API
                // System.out.println(apiOutput);

                // Conerting to JSON
                ObjectMapper mapper = new ObjectMapper();
                jsonNode = mapper.readTree(apiOutput);

                if (jsonNode != null)
                {
                    JsonNode rootNode = jsonNode.path("value");
                    if (rootNode != null)
                    {
                        System.out.println("Customizing Bound!!");
                        List<TY_CaseCatalogCustomizing> caseCusList = new ArrayList<TY_CaseCatalogCustomizing>();

                        Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                        while (payloadItr.hasNext())
                        {
                            // System.out.println("Payload Iterator Bound");
                            Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                            String payloadFieldName = payloadEnt.getKey();
                            // System.out.println("Payload Field Scanned: " + payloadFieldName);

                            if (payloadFieldName.equals("value"))
                            {
                                Iterator<JsonNode> cusItr = payloadEnt.getValue().elements();
                                // System.out.println("Cases Iterator Bound");
                                while (cusItr.hasNext())
                                {

                                    JsonNode cusEnt = cusItr.next();
                                    if (cusEnt != null)
                                    {
                                        String caseTypePL = null, statusSchema = null, status = null,
                                                partyScheme = null, cataglogId = null;

                                        Iterator<String> fieldNames = cusEnt.fieldNames();
                                        while (fieldNames.hasNext())
                                        {
                                            String cusFieldName = fieldNames.next();
                                            // System.out.println("Case Entity Field Scanned: " + caseFieldName);
                                            if (cusFieldName.equals("caseType"))
                                            {
                                                // System.out.println("Case GUID Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    caseTypePL = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("statusSchema"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    statusSchema = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("status"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    status = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("partyScheme"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    partyScheme = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("catalog"))
                                            {
                                                // System.out.println("Inside Admin Data: " );

                                                JsonNode catEnt = cusEnt.path("catalog");
                                                if (catEnt != null)
                                                {
                                                    // System.out.println("AdminData Node Bound");

                                                    Iterator<String> fieldNamesCat = catEnt.fieldNames();
                                                    while (fieldNamesCat.hasNext())
                                                    {
                                                        String catFieldName = fieldNamesCat.next();
                                                        if (catFieldName.equals("id"))
                                                        {
                                                            // System.out.println( "Created On : " +
                                                            // admEnt.get(admFieldName).asText());
                                                            cataglogId = catEnt.get(catFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                        }

                                        if (StringUtils.hasText(cataglogId) && StringUtils.hasText(caseTypePL))
                                        {

                                            caseCusList.add(new TY_CaseCatalogCustomizing(caseTypePL, statusSchema,
                                                    status, partyScheme, cataglogId));

                                        }

                                    }

                                }

                            }

                        }

                        // Get the Active Catalog Assignment
                        if (CollectionUtils.isNotEmpty(caseCusList))
                        {
                            Optional<TY_CaseCatalogCustomizing> caseCusO = caseCusList.stream()
                                    .filter(r -> r.getStatus().equals(GC_Constants.gc_statusACTIVE)).findFirst();
                            if (caseCusO.isPresent())
                            {
                                caseCus = caseCusO.get();
                            }
                        }
                    }

                }

            }
        }

        catch (Exception e)
        {
            throw new EX_ESMAPI(msgSrc.getMessage("ERR_CATG_LOAD_CASETYP", new Object[]
            { caseType, e.getMessage() }, Locale.ENGLISH));

        }
        finally
        {
            httpClient.close();
        }

        return caseCus;
    }

    @Override
    public List<TY_CatalogItem> getActiveCaseCategoriesByCatalogId(String catalogID) throws EX_ESMAPI, IOException
    {
        List<TY_CatalogItem> catgTree = null;
        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String urlLink = null;

        try
        {
            if (StringUtils.hasLength(srvCloudUrls.getUserName()) && StringUtils.hasLength(srvCloudUrls.getPassword())
                    && StringUtils.hasLength(srvCloudUrls.getCatgTreeUrl()))
            {
                System.out.println("Url and Credentials Found!!");

                urlLink = StringsUtility.replaceURLwithParams(srvCloudUrls.getCatgTreeUrl(), new String[]
                { catalogID }, GC_Constants.gc_UrlReplParam);

                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                // Query URL Encoding to avoid Illegal character error in Query
                URL url = new URL(urlLink);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(),
                        url.getPath(), url.getQuery(), url.getRef());
                String correctEncodedURL = uri.toASCIIString();

                HttpGet httpGet = new HttpGet(correctEncodedURL);

                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpGet.addHeader("accept", "application/json");

                // Fire the Url
                response = httpClient.execute(httpGet);

                // verify the valid error code first
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK)
                {

                    if (statusCode == HttpStatus.SC_NOT_FOUND)
                    {
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_CATALOG_READ", new Object[]
                        { catalogID }, Locale.ENGLISH));
                    }
                    else
                    {
                        throw new RuntimeException("Failed with HTTP error code : " + statusCode);
                    }

                }

                // Try and Get Entity from Response
                HttpEntity entity = response.getEntity();
                String apiOutput = EntityUtils.toString(entity);
                // Lets see what we got from API
                // System.out.println(apiOutput);

                // Conerting to JSON
                ObjectMapper mapper = new ObjectMapper();
                jsonNode = mapper.readTree(apiOutput);

                if (jsonNode != null)
                {

                    JsonNode rootNode = jsonNode.path("value");
                    if (rootNode != null)
                    {
                        System.out.println("Customizing Bound!!");
                        catgTree = new ArrayList<TY_CatalogItem>();

                        Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                        while (payloadItr.hasNext())
                        {
                            // System.out.println("Payload Iterator Bound");
                            Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                            String payloadFieldName = payloadEnt.getKey();
                            // System.out.println("Payload Field Scanned: " + payloadFieldName);

                            if (payloadFieldName.equals("value"))
                            {
                                Iterator<JsonNode> cusItr = payloadEnt.getValue().elements();
                                // System.out.println("Cases Iterator Bound");
                                while (cusItr.hasNext())
                                {

                                    JsonNode cusEnt = cusItr.next();
                                    if (cusEnt != null)
                                    {
                                        String id = null, parentId = null, name = null, parentName = null;

                                        Iterator<String> fieldNames = cusEnt.fieldNames();
                                        while (fieldNames.hasNext())
                                        {
                                            String cusFieldName = fieldNames.next();
                                            // System.out.println("Case Entity Field Scanned: " + caseFieldName);
                                            if (cusFieldName.equals("id"))
                                            {
                                                // System.out.println("Case GUID Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    id = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("parentId"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    parentId = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("name"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    name = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                            if (cusFieldName.equals("parentName"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(cusEnt.get(cusFieldName).asText()))
                                                {
                                                    parentName = cusEnt.get(cusFieldName).asText();
                                                }
                                            }

                                        }

                                        if (StringUtils.hasText(id))
                                        {

                                            catgTree.add(new TY_CatalogItem(id, name, parentId, parentName));

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
            throw new EX_ESMAPI(msgSrc.getMessage("ERR_CATALOG_READ", new Object[]
            { catalogID, e.getMessage() }, Locale.ENGLISH));

        }
        finally
        {
            httpClient.close();
        }

        return catgTree;
    }

    @Override
    public String createNotes(TY_NotesCreate notes) throws EX_ESMAPI
    {
        String noteId = null;

        if (StringUtils.hasText(notes.getHtmlContent()))
        {
            HttpClient httpclient = HttpClients.createDefault();
            String notesPOSTURL = srvCloudUrls.getNotesUrl();
            if (StringUtils.hasText(notesPOSTURL))
            {
                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());
                HttpPost httpPost = new HttpPost(notesPOSTURL);
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpPost.addHeader("Content-Type", "application/json");

                ObjectMapper objMapper = new ObjectMapper();
                try
                {
                    String requestBody = objMapper.writeValueAsString(notes);
                    System.out.println(requestBody);

                    StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    httpPost.setEntity(entity);

                    // POST Notes in Service Cloud
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

                        // Conerting to JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(apiOutput);

                        if (jsonNode != null)
                        {

                            JsonNode rootNode = jsonNode.path("value");
                            if (rootNode != null)
                            {

                                System.out.println("Notes Bound!!");

                                Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                                while (payloadItr.hasNext())
                                {
                                    System.out.println("Payload Iterator Bound");
                                    Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                    String payloadFieldName = payloadEnt.getKey();
                                    System.out.println("Payload Field Scanned:  " + payloadFieldName);

                                    if (payloadFieldName.equals("value"))
                                    {
                                        JsonNode notesEnt = payloadEnt.getValue();
                                        System.out.println("New Notes Entity Bound");
                                        if (notesEnt != null)
                                        {

                                            System.out.println("Notes Entity Bound - Reading Notes...");
                                            Iterator<String> fieldNames = notesEnt.fieldNames();
                                            while (fieldNames.hasNext())
                                            {
                                                String notesFieldName = fieldNames.next();
                                                System.out.println("Notes Entity Field Scanned:  " + notesFieldName);
                                                if (notesFieldName.equals("id"))
                                                {
                                                    System.out.println("Notes GUID Added : "
                                                            + notesEnt.get(notesFieldName).asText());
                                                    if (StringUtils.hasText(notesEnt.get(notesFieldName).asText()))
                                                    {
                                                        noteId = notesEnt.get(notesFieldName).asText();

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
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_NOTES_POST", new Object[]
                        { e.getLocalizedMessage() }, Locale.ENGLISH));
                    }
                }
                catch (JsonProcessingException e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("ERR_NEW_NOTES_JSON", new Object[]
                    { e.getLocalizedMessage() }, Locale.ENGLISH));
                }

            }

        }

        return noteId;
    }

    @Override
    public String createCase(TY_Case_SrvCloud caseEntity) throws EX_ESMAPI
    {
        String caseId = null;

        if (StringUtils.hasText(caseEntity.getAccount().getId()))
        {
            HttpClient httpclient = HttpClients.createDefault();
            String casePOSTURL = getPOSTURL4BaseUrl(srvCloudUrls.getCasesUrl());
            if (StringUtils.hasText(casePOSTURL))
            {
                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());
                HttpPost httpPost = new HttpPost(casePOSTURL);
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpPost.addHeader("Content-Type", "application/json");

                ObjectMapper objMapper = new ObjectMapper();
                try
                {
                    String requestBody = objMapper.writeValueAsString(caseEntity);
                    log.info(requestBody);

                    StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    httpPost.setEntity(entity);

                    // POST Case in Service Cloud
                    try
                    {
                        // Fire the Url
                        HttpResponse response = httpclient.execute(httpPost);
                        // verify the valid error code first
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode != HttpStatus.SC_CREATED)
                        {
                            HttpEntity entityResp = response.getEntity();
                            String apiOutput = EntityUtils.toString(entityResp);
                            System.out.println(apiOutput);
                            throw new RuntimeException(
                                    "Failed with HTTP error code : " + statusCode + " Details: " + apiOutput);

                        }

                        // Try and Get Entity from Response
                        HttpEntity entityResp = response.getEntity();
                        String apiOutput = EntityUtils.toString(entityResp);

                        // Conerting to JSON
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(apiOutput);

                        if (jsonNode != null)
                        {

                            JsonNode rootNode = jsonNode.path("value");
                            if (rootNode != null)
                            {

                                System.out.println("Notes Bound!!");

                                Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                                while (payloadItr.hasNext())
                                {
                                    System.out.println("Payload Iterator Bound");
                                    Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                    String payloadFieldName = payloadEnt.getKey();
                                    System.out.println("Payload Field Scanned:  " + payloadFieldName);

                                    if (payloadFieldName.equals("value"))
                                    {
                                        JsonNode caseEnt = payloadEnt.getValue();
                                        System.out.println("New Case Entity Bound");
                                        if (caseEnt != null)
                                        {

                                            System.out.println("Case Entity Bound - Reading Case...");
                                            Iterator<String> fieldNames = caseEnt.fieldNames();
                                            while (fieldNames.hasNext())
                                            {
                                                String caseFieldName = fieldNames.next();
                                                System.out.println("Case Entity Field Scanned:  " + caseFieldName);
                                                if (caseFieldName.equals("displayId"))
                                                {
                                                    System.out.println(
                                                            "Case ID Added : " + caseEnt.get(caseFieldName).asText());
                                                    if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                    {
                                                        caseId = caseEnt.get(caseFieldName).asText();

                                                    }
                                                    break;
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
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_NOTES_POST", new Object[]
                        { e.getLocalizedMessage() }, Locale.ENGLISH));
                    }
                }
                catch (JsonProcessingException e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("ERR_NEW_NOTES_JSON", new Object[]
                    { e.getLocalizedMessage() }, Locale.ENGLISH));
                }

            }

        }

        return caseId;
    }

    @Override
    public TY_AttachmentResponse createAttachment(TY_Attachment attachment) throws EX_ESMAPI
    {
        TY_AttachmentResponse attR = null;

        if (attachment != null)
        {
            // Populate the Attachment POJO for getting the POST Url for Saving the
            // attachment
            if (StringUtils.hasText(attachment.getFileName()))
            {
                HttpClient httpclient = HttpClients.createDefault();
                String docPOSTURL = srvCloudUrls.getDocSrvUrl();

                // Call Attachment POST to generate the Document Store Url
                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());
                HttpPost httpPost = new HttpPost(docPOSTURL);
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpPost.addHeader("Content-Type", "application/json");

                ObjectMapper objMapper = new ObjectMapper();
                String requestBody;
                try
                {
                    requestBody = objMapper.writeValueAsString(attachment);
                    log.info(requestBody);

                    if (requestBody != null)
                    {
                        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                        httpPost.setEntity(entity);
                        // POST Notes in Service Cloud
                        try
                        {
                            // Fire the Url
                            HttpResponse response = httpclient.execute(httpPost);

                            // verify the valid error code first
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode != HttpStatus.SC_CREATED && statusCode != HttpStatus.SC_OK)
                            {
                                throw new RuntimeException("Failed with HTTP error code : " + statusCode + " Message - "
                                        + response.getStatusLine().toString());
                            }

                            // Try and Get Entity from Response
                            HttpEntity entityResp = response.getEntity();
                            String apiOutput = EntityUtils.toString(entityResp);

                            // Conerting to JSON
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(apiOutput);

                            if (jsonNode != null)
                            {
                                JsonNode rootNode = jsonNode.path("value");
                                if (rootNode != null)
                                {

                                    log.info("Attachments Bound!!");

                                    Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                                    while (payloadItr.hasNext())
                                    {
                                        log.info("Payload Iterator Bound");
                                        Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                        String payloadFieldName = payloadEnt.getKey();
                                        log.info("Payload Field Scanned:  " + payloadFieldName);

                                        if (payloadFieldName.equals("value"))
                                        {
                                            JsonNode attEnt = payloadEnt.getValue();
                                            log.info("New Attachment Entity Bound");
                                            if (attEnt != null)
                                            {
                                                // Initailize Response Entity
                                                attR = new TY_AttachmentResponse();

                                                log.info("Attachments Entity Bound - Reading Attachments Response...");
                                                Iterator<String> fieldNames = attEnt.fieldNames();
                                                while (fieldNames.hasNext())
                                                {
                                                    String attFieldName = fieldNames.next();
                                                    log.info("Notes Entity Field Scanned:  " + attFieldName);

                                                    // attachment ID
                                                    if (attFieldName.equals("id"))
                                                    {
                                                        log.info("Attachment GUID Added : "
                                                                + attEnt.get(attFieldName).asText());
                                                        if (StringUtils.hasText(attEnt.get(attFieldName).asText()))
                                                        {
                                                            attR.setId(attEnt.get(attFieldName).asText());

                                                        }
                                                    }

                                                    // attachment Upload URL
                                                    if (attFieldName.equals("uploadUrl"))
                                                    {
                                                        log.info("Attachment Upload Url Added : "
                                                                + attEnt.get(attFieldName).asText());
                                                        if (StringUtils.hasText(attEnt.get(attFieldName).asText()))
                                                        {
                                                            attR.setUploadUrl(attEnt.get(attFieldName).asText());

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
                            throw new EX_ESMAPI(msgSrc.getMessage("ERR_DOCS_POST", new Object[]
                            { e.getLocalizedMessage() }, Locale.ENGLISH));
                        }

                    }
                }
                catch (JsonProcessingException e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("ERR_NEW_DOCS_JSON", new Object[]
                    { e.getLocalizedMessage(), attachment.toString() }, Locale.ENGLISH));
                }

            }
        }

        return attR;

    }

    @Override
    public boolean persistAttachment(String url, MultipartFile file) throws EX_ESMAPI, IOException
    {
        boolean isPersisted = false;
        if (StringUtils.hasText(url))
        {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(url);
            if (httpPut != null)
            {
                ByteArrayEntity requestEntity = new ByteArrayEntity(file.getBytes());
                if (requestEntity != null)
                {
                    httpPut.setEntity(requestEntity);

                    // Fire the Url
                    HttpResponse response = httpclient.execute(httpPut);
                    // verify the valid error code first
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK)
                    {
                        isPersisted = true;
                    }
                    else
                    {
                        HttpEntity entityResp = response.getEntity();
                        String apiOutput = EntityUtils.toString(entityResp);
                        log.error(apiOutput);
                        throw new EX_ESMAPI("Error peristing Attachment for filename : " + file.getOriginalFilename()
                                + "HTTPSTATUS Code" + statusCode + "Details :" + apiOutput);
                    }

                }
            }

        }

        return isPersisted;
    }

    @Override
    public String getEmployeeIdByUserId(String userId) throws EX_ESMAPI
    {

        String empID = null;
        Map<String, String> empUserIds = new HashMap<String, String>();
        if (StringUtils.hasText(userId) && srvCloudUrls != null)
        {
            if (StringUtils.hasText(srvCloudUrls.getEmplUrl()))
            {
                try
                {
                    JsonNode empResp = getAllEmployees();
                    if (empResp != null)
                    {
                        JsonNode rootNode = empResp.path("value");
                        if (rootNode != null)
                        {
                            System.out.println("Employees Bound!!");

                            Iterator<Map.Entry<String, JsonNode>> payloadItr = empResp.fields();
                            while (payloadItr.hasNext())
                            {
                                System.out.println("Payload Iterator Bound");
                                Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                                String payloadFieldName = payloadEnt.getKey();
                                System.out.println("Payload Field Scanned:  " + payloadFieldName);

                                if (payloadFieldName.equals("value"))
                                {
                                    Iterator<JsonNode> empItr = payloadEnt.getValue().elements();
                                    System.out.println("Employee Iterator Bound");
                                    while (empItr.hasNext())
                                    {

                                        JsonNode empEnt = empItr.next();
                                        if (empEnt != null)
                                        {
                                            String empid = null, empUserId = null;
                                            System.out.println("Employee Entity Bound - Reading Employee...");
                                            Iterator<String> fieldNames = empEnt.fieldNames();
                                            while (fieldNames.hasNext())
                                            {
                                                String empFieldName = fieldNames.next();
                                                System.out.println("Employee Entity Field Scanned:  " + empFieldName);
                                                if (empFieldName.equals("id"))
                                                {
                                                    System.out.println(
                                                            "Employee Id Added : " + empEnt.get(empFieldName).asText());
                                                    empid = empEnt.get(empFieldName).asText();
                                                }

                                                if (empFieldName.equals("employeeDisplayId"))
                                                {
                                                    System.out.println("Employee User Id Added : "
                                                            + empEnt.get(empFieldName).asText());
                                                    empUserId = empEnt.get(empFieldName).asText();
                                                }

                                            }
                                            // avoid null email accounts
                                            if (StringUtils.hasText(empid) && StringUtils.hasText(empUserId))
                                            {
                                                empUserIds.put(empid, empUserId);
                                            }

                                        }

                                    }

                                }

                            }

                            // Filter by Email
                            Optional<Map.Entry<String, String>> OptionalEmp = empUserIds.entrySet().stream()
                                    .filter(u -> u.getValue().equals(userId)).findFirst();
                            if (OptionalEmp.isPresent())
                            {
                                Map.Entry<String, String> employee = OptionalEmp.get();
                                empID = employee.getKey(); // Return Account ID
                            }

                        }
                    }
                }
                catch (IOException e)
                {
                    throw new EX_ESMAPI(msgSrc.getMessage("API_EMP_ERROR", new Object[]
                    { e.getLocalizedMessage() }, Locale.ENGLISH));
                }
            }

        }
        return empID;
    }

    @Override
    public List<TY_CaseESS> getCases4User(Ty_UserAccountContactEmployee userDetails) throws IOException
    {
        List<TY_CaseESS> casesESSList = null;

        List<TY_CaseESS> casesESSList4User = null;

        try
        {
            if (StringUtils.hasText(userDetails.getAccountId()) || StringUtils.hasText(userDetails.getEmployeeId()))
            {

                JsonNode jsonNode = getAllCases();

                if (jsonNode != null)
                {

                    JsonNode rootNode = jsonNode.path("value");
                    if (rootNode != null)
                    {
                        System.out.println("Cases Bound!!");
                        casesESSList = new ArrayList<TY_CaseESS>();

                        Iterator<Map.Entry<String, JsonNode>> payloadItr = jsonNode.fields();
                        while (payloadItr.hasNext())
                        {
                            // System.out.println("Payload Iterator Bound");
                            Map.Entry<String, JsonNode> payloadEnt = payloadItr.next();
                            String payloadFieldName = payloadEnt.getKey();
                            // System.out.println("Payload Field Scanned: " + payloadFieldName);

                            if (payloadFieldName.equals("value"))
                            {
                                Iterator<JsonNode> casesItr = payloadEnt.getValue().elements();
                                // System.out.println("Cases Iterator Bound");
                                while (casesItr.hasNext())
                                {

                                    JsonNode caseEnt = casesItr.next();
                                    if (caseEnt != null)
                                    {
                                        String caseid = null, caseguid = null, caseType = null,
                                                caseTypeDescription = null, subject = null, status = null,
                                                createdOn = null, accountId = null, contactId = null, origin = null;
                                        // System.out.println("Cases Entity Bound - Reading Case...");
                                        Iterator<String> fieldNames = caseEnt.fieldNames();
                                        while (fieldNames.hasNext())
                                        {
                                            String caseFieldName = fieldNames.next();
                                            // System.out.println("Case Entity Field Scanned: " + caseFieldName);
                                            if (caseFieldName.equals("id"))
                                            {
                                                // System.out.println("Case GUID Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseguid = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("displayId"))
                                            {
                                                // System.out.println("Case Id Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseid = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("caseType"))
                                            {
                                                // System.out.println("Case Type Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseType = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("caseTypeDescription"))
                                            {
                                                // System.out.println("Case Type Description Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    caseTypeDescription = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("subject"))
                                            {
                                                // System.out.println("Case Subject Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    subject = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("origin"))
                                            {
                                                // System.out.println("Case Subject Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    origin = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("statusDescription"))
                                            {
                                                // System.out.println("Case Status Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    status = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("statusDescription"))
                                            {
                                                // System.out.println("Case Status Added : " +
                                                // caseEnt.get(caseFieldName).asText());
                                                if (StringUtils.hasText(caseEnt.get(caseFieldName).asText()))
                                                {
                                                    status = caseEnt.get(caseFieldName).asText();
                                                }
                                            }

                                            if (caseFieldName.equals("adminData"))
                                            {
                                                // System.out.println("Inside Admin Data: " );

                                                JsonNode admEnt = caseEnt.path("adminData");
                                                if (admEnt != null)
                                                {
                                                    // System.out.println("AdminData Node Bound");

                                                    Iterator<String> fieldNamesAdm = admEnt.fieldNames();
                                                    while (fieldNamesAdm.hasNext())
                                                    {
                                                        String admFieldName = fieldNamesAdm.next();
                                                        if (admFieldName.equals("createdOn"))
                                                        {
                                                            // System.out.println( "Created On : " +
                                                            // admEnt.get(admFieldName).asText());
                                                            createdOn = admEnt.get(admFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                            if (caseFieldName.equals("account"))
                                            {
                                                // System.out.println("Inside Account: " );

                                                JsonNode accEnt = caseEnt.path("account");
                                                if (accEnt != null)
                                                {
                                                    // System.out.println("Account Node Bound");

                                                    Iterator<String> fieldNamesAcc = accEnt.fieldNames();
                                                    while (fieldNamesAcc.hasNext())
                                                    {
                                                        String accFieldName = fieldNamesAcc.next();
                                                        if (accFieldName.equals("id"))
                                                        {
                                                            // System.out.println(
                                                            // "Account ID : " + accEnt.get(accFieldName).asText());
                                                            accountId = accEnt.get(accFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                            if (caseFieldName.equals("individualCustomer")
                                                    && (!StringUtils.hasText(accountId)))
                                            {
                                                // System.out.println("Inside Account: " );

                                                JsonNode accEnt = caseEnt.path("individualCustomer");
                                                if (accEnt != null)
                                                {
                                                    // System.out.println("Account Node Bound");

                                                    Iterator<String> fieldNamesAcc = accEnt.fieldNames();
                                                    while (fieldNamesAcc.hasNext())
                                                    {
                                                        String accFieldName = fieldNamesAcc.next();
                                                        if (accFieldName.equals("id"))
                                                        {
                                                            // System.out.println(
                                                            // "Account ID : " + accEnt.get(accFieldName).asText());
                                                            accountId = accEnt.get(accFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                            if (caseFieldName.equals("reporter"))
                                            {
                                                // System.out.println("Inside Reporter: " );

                                                JsonNode repEnt = caseEnt.path("reporter");
                                                if (repEnt != null)
                                                {
                                                    // System.out.println("Reporter Node Bound");

                                                    Iterator<String> fieldNamesRep = repEnt.fieldNames();
                                                    while (fieldNamesRep.hasNext())
                                                    {
                                                        String repFieldName = fieldNamesRep.next();
                                                        if (repFieldName.equals("id"))
                                                        {
                                                            // System.out.println(
                                                            // "Reporter ID : " + repEnt.get(repFieldName).asText());
                                                            contactId = repEnt.get(repFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }

                                        }

                                        if (StringUtils.hasText(caseid) && StringUtils.hasText(caseguid))
                                        {
                                            if (StringUtils.hasText(createdOn))
                                            {
                                                // Parse the date-time string into OffsetDateTime
                                                OffsetDateTime odt = OffsetDateTime.parse(createdOn);
                                                // Convert OffsetDateTime into Instant
                                                Instant instant = odt.toInstant();
                                                // If at all, you need java.util.Date
                                                Date date = Date.from(instant);

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                                                String dateFormatted = sdf.format(date);

                                                casesESSList.add(new TY_CaseESS(caseguid, caseid, caseType,
                                                        caseTypeDescription, subject, status, accountId, contactId,
                                                        createdOn, date, dateFormatted, origin));

                                            }
                                            else
                                            {
                                                casesESSList.add(new TY_CaseESS(caseguid, caseid, caseType,
                                                        caseTypeDescription, subject, status, accountId, contactId,
                                                        createdOn, null, null, origin));
                                            }

                                        }

                                    }

                                }

                            }

                        }
                    }

                }

            }
            else
            {
                return null;
            }

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*
         * ------- FILTER FOR USER ACCOUNT or REPORTED BY CONTACT PERSON
         */

        if (!CollectionUtils.isEmpty(casesESSList))
        {
            casesESSList4User = casesESSList.stream().filter(e ->
            {
                // #ESMModule
                // If no Account Itself in Present in Case - Ignore Such Cases --Add Employee
                // with an and condition once ESM module is enabled
                if (!StringUtils.hasText(e.getAccountId()))
                {
                    return false;
                }

                if (StringUtils.hasText(e.getContactId()))
                {

                    if (e.getAccountId().equals(userDetails.getAccountId())
                            || e.getContactId().equals(userDetails.getContactId()))
                    {
                        return true;
                    }

                }
                else
                {
                    if (e.getAccountId().equals(userDetails.getAccountId()))
                    {
                        return true;
                    }

                }
                return false;

            }).collect(Collectors.toList());

        }

        if (!CollectionUtils.isEmpty(casesESSList4User))
        {
            log.info("# Cases returned in call : " + casesESSList4User.size());
        }
        return casesESSList4User;
    }

    @Override
    public List<TY_KeyValue> getVHelpDDLB4Field(String fieldName) throws EX_ESMAPI, IOException
    {
        List<TY_KeyValue> vhlbDDLB = null;

        JsonNode jsonNode = null;
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String urlLink = null;
        try
        {
            if (StringUtils.hasText(fieldName) && StringUtils.hasText(srvCloudUrls.getVhlpUrl()))

            {
                log.info("Invoking Value help for FieldName : " + fieldName);

                urlLink = srvCloudUrls.getVhlpUrl() + fieldName;

                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                HttpGet httpGet = new HttpGet(urlLink);

                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpGet.addHeader("accept", "application/json");

                // Fire the Url
                response = httpClient.execute(httpGet);

                // verify the valid error code first
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK)
                {

                    if (statusCode == HttpStatus.SC_NOT_FOUND)
                    {
                        String msg = msgSrc.getMessage("ERR_VHLP_FLD_SRVCLOUD_NOTFOUND", new Object[]
                        { fieldName }, Locale.ENGLISH);
                        log.error(msg);
                        throw new EX_ESMAPI(msg);
                    }
                    else
                    {
                        String msg = msgSrc.getMessage("ERR_VHLP_FLD_SRVCLOUD_GEN", new Object[]
                        { fieldName, statusCode }, Locale.ENGLISH);
                        log.error(msg);
                        throw new EX_ESMAPI(msg);

                    }

                }

                // Try and Get Entity from Response
                HttpEntity entity = response.getEntity();
                String apiOutput = EntityUtils.toString(entity);
                // Lets see what we got from API
                // System.out.println(apiOutput);

                // Conerting to JSON
                ObjectMapper mapper = new ObjectMapper();
                jsonNode = mapper.readTree(apiOutput);

                if (jsonNode != null)
                {

                    JsonNode rootNode = jsonNode.path("value");
                    if (rootNode != null)
                    {
                        JsonNode contentNode = rootNode.at("/content");
                        if (contentNode != null && contentNode.isArray() && contentNode.size() > 0)
                        {
                            log.info("Values Bound for Value Help for Field -  " + fieldName);
                            vhlbDDLB = new ArrayList<TY_KeyValue>();
                            for (JsonNode arrayItem : contentNode)
                            {
                                String code = null, desc = null;
                                Boolean isActive = true;
                                Iterator<Entry<String, JsonNode>> fields = arrayItem.fields();
                                while (fields.hasNext())
                                {
                                    Entry<String, JsonNode> jsonField = fields.next();
                                    if (jsonField.getKey().equals("code"))
                                    {
                                        code = jsonField.getValue().asText();
                                    }

                                    if (jsonField.getKey().equals("description"))
                                    {
                                        desc = jsonField.getValue().asText();
                                    }

                                    if (jsonField.getKey().equals("active"))
                                    {
                                        isActive = jsonField.getValue().asBoolean();
                                    }

                                }

                                if (StringUtils.hasText(code) && StringUtils.hasText(desc) && isActive)
                                {
                                    TY_KeyValue keyVal = new TY_KeyValue(code, desc);
                                    vhlbDDLB.add(keyVal);
                                }
                            }
                        }

                    }

                }
            }
        }

        catch (Exception e)
        {
            throw new EX_ESMAPI(msgSrc.getMessage("ERR_VHLP_FLD_SRVCLOUD_NOTFOUND", new Object[]
            { fieldName, e.getMessage() }, Locale.ENGLISH));

        }
        finally
        {
            httpClient.close();
        }

        return vhlbDDLB;
    }

    @Override
    public List<TY_CaseESS> getCases4User(Ty_UserAccountContactEmployee userDetails, EnumCaseTypes caseType)
            throws IOException
    {

        List<TY_CaseESS> casesByCaseType = null;
        List<TY_CaseESS> allCases = this.getCases4User(userDetails);
        if (caseTypeCus != null)
        {
            Optional<TY_CatgCusItem> cusO = caseTypeCus.getCustomizations().stream()
                    .filter(c -> c.getCaseTypeEnum().equals(caseType)).findFirst();
            if (cusO.isPresent() && CollectionUtils.isNotEmpty(allCases))
            {
                casesByCaseType = allCases.stream().filter(t -> t.getCaseType().equals(cusO.get().getCaseType()))
                        .collect(Collectors.toList());
            }
        }
        if (CollectionUtils.isNotEmpty(casesByCaseType))
        {
            log.info("# Cases for Case Type - " + caseType.name() + "for Current User : " + casesByCaseType.size());
        }
        else
        {
            log.info("No Cases Found for the User - " + userDetails.getUserId()
                    + ". Probable Account Creation Directly in Service Cloud!");
        }

        return casesByCaseType;
    }

    @Override
    public TY_CaseDetails getCaseDetails4Case(String caseId) throws EX_ESMAPI, IOException
    {
        TY_CaseDetails caseDetails = null;
        if (StringUtils.hasText(caseId))
        {
            JsonNode jsonNode = null;
            HttpResponse response = null;
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            String urlLink = null;
            try
            {
                if (StringUtils.hasText(caseId) && StringUtils.hasText(srvCloudUrls.getCaseDetailsUrl()))

                {
                    log.info("Fetching Details for Case ID : " + caseId);

                    urlLink = srvCloudUrls.getCaseDetailsUrl() + caseId;

                    String encoding = Base64.getEncoder()
                            .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                    HttpGet httpGet = new HttpGet(urlLink);

                    httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                    httpGet.addHeader("accept", "application/json");

                    // Fire the Url
                    response = httpClient.execute(httpGet);

                    // verify the valid error code first
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK)
                    {
                        String msg = msgSrc.getMessage("ERR_CASE_DET_FETCH", new Object[]
                        { caseId }, Locale.ENGLISH);
                        log.error(msg);
                        throw new EX_ESMAPI(msg);
                    }

                    // Try and Get Entity from Response
                    HttpEntity entity = response.getEntity();
                    String apiOutput = EntityUtils.toString(entity);
                    // Lets see what we got from API
                    // System.out.println(apiOutput);

                    // Get Response Header(s) from API REsponse
                    Header[] headers = response.getAllHeaders();
                    String eTag = null;
                    if (headers.length > 0)
                    {
                        // Get the Etag
                        Optional<Header> etagO = Arrays.asList(headers).stream()
                                .filter(e -> e.getName().equals(GC_Constants.gc_ETag)).findFirst();
                        if (etagO.isPresent())
                        {
                            eTag = etagO.get().getValue();
                        }
                    }

                    // Conerting to JSON
                    ObjectMapper mapper = new ObjectMapper();
                    jsonNode = mapper.readTree(apiOutput);

                    if (jsonNode != null)
                    {

                        JsonNode rootNode = jsonNode.path("value");
                        if (rootNode != null)
                        {
                            caseDetails = new TY_CaseDetails();
                            caseDetails.setCaseGuid(caseId);
                            caseDetails.setETag(eTag);
                            caseDetails.setNotes(new ArrayList<TY_NotesDetails>());

                            // Add Notes
                            JsonNode contentNode = rootNode.at("/notes");
                            if (contentNode != null && contentNode.isArray() && contentNode.size() > 0)
                            {
                                log.info("Notes for Case ID : " + caseId + " bound..");
                                for (JsonNode arrayItem : contentNode)
                                {
                                    String content = null, noteType = null, userCreate = null, timestamp = null,
                                            id = null, noteId = null;
                                    OffsetDateTime odt = null;

                                    Iterator<Entry<String, JsonNode>> fields = arrayItem.fields();
                                    while (fields.hasNext())
                                    {
                                        Entry<String, JsonNode> jsonField = fields.next();
                                        if (jsonField.getKey().equals("id"))
                                        {
                                            id = jsonField.getValue().asText();
                                        }

                                        if (jsonField.getKey().equals("noteId"))
                                        {
                                            noteId = jsonField.getValue().asText();
                                        }

                                        if (jsonField.getKey().equals("content"))
                                        {
                                            content = jsonField.getValue().asText();
                                        }

                                        if (jsonField.getKey().equals("noteType"))
                                        {
                                            noteType = jsonField.getValue().asText();
                                        }

                                        if (jsonField.getKey().equals("adminData"))
                                        {
                                            JsonNode adminNode = jsonField.getValue();
                                            if (adminNode != null)
                                            {
                                                Iterator<String> fieldNames = adminNode.fieldNames();
                                                while (fieldNames.hasNext())
                                                {
                                                    String caseFieldName = fieldNames.next();

                                                    if (caseFieldName.equals("createdOn"))
                                                    {

                                                        if (StringUtils.hasText(adminNode.get(caseFieldName).asText()))
                                                        {

                                                            timestamp = adminNode.get(caseFieldName).asText();
                                                            // Parse the date-time string into OffsetDateTime
                                                            odt = OffsetDateTime.parse(timestamp);
                                                        }
                                                    }

                                                    if (caseFieldName.equals("createdByName"))
                                                    {

                                                        if (StringUtils.hasText(adminNode.get(caseFieldName).asText()))
                                                        {
                                                            userCreate = adminNode.get(caseFieldName).asText();
                                                        }
                                                    }

                                                }
                                            }
                                        }

                                    }
                                    TY_NotesDetails newNote = new TY_NotesDetails(noteType, id, noteId, odt, userCreate,
                                            content);
                                    caseDetails.getNotes().add(newNote);

                                }

                            }

                            // Add Description
                            JsonNode descNode = rootNode.at("/description");
                            if (descNode != null && descNode.size() > 0)
                            {
                                log.info("Desc for Case ID : " + caseId + " bound..");

                                Iterator<String> fieldNamesDesc = descNode.fieldNames();
                                String content = null, noteType = null, userCreate = null, timestamp = null, id = null,
                                        noteId = null;
                                OffsetDateTime odt = null;
                                while (fieldNamesDesc.hasNext())
                                {
                                    String descFieldName = fieldNamesDesc.next();
                                    if (descFieldName.equals("id"))
                                    {
                                        id = descNode.get(descFieldName).asText();
                                    }

                                    if (descFieldName.equals("noteId"))
                                    {
                                        noteId = descNode.get(descFieldName).asText();
                                    }

                                    if (descFieldName.equals("content"))
                                    {
                                        content = descNode.get(descFieldName).asText();
                                    }

                                    if (descFieldName.equals("noteType"))
                                    {
                                        noteType = descNode.get(descFieldName).asText();
                                    }

                                    if (descFieldName.equals("adminData"))
                                    {
                                        // System.out.println("Inside Reporter: " );

                                        JsonNode admEnt = descNode.path("adminData");
                                        if (admEnt != null)
                                        {
                                            // System.out.println("Reporter Node Bound");

                                            Iterator<String> fieldNamesAdm = admEnt.fieldNames();
                                            while (fieldNamesAdm.hasNext())
                                            {
                                                String admFieldName = fieldNamesAdm.next();
                                                if (admFieldName.equals("createdOn"))
                                                {
                                                    if (StringUtils.hasText(admEnt.get(admFieldName).asText()))
                                                    {

                                                        timestamp = admEnt.get(admFieldName).asText();
                                                        // Parse the date-time string into OffsetDateTime
                                                        odt = OffsetDateTime.parse(timestamp);
                                                    }
                                                }

                                                if (admFieldName.equals("createdByName"))
                                                {

                                                    if (StringUtils.hasText(admEnt.get(admFieldName).asText()))
                                                    {
                                                        userCreate = admEnt.get(admFieldName).asText();
                                                    }
                                                }

                                            }

                                        }
                                    }

                                }
                                TY_NotesDetails newNote = new TY_NotesDetails(noteType, id, noteId, odt, userCreate,
                                        content);
                                caseDetails.getNotes().add(newNote);

                            }

                        }
                    }

                }
            }
            catch (Exception e)
            {
                throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_DET_FETCH", new Object[]
                { caseId, e.getMessage() }, Locale.ENGLISH));

            }
            finally
            {
                httpClient.close();
            }

        }
        return caseDetails;
    }

    @Override
    public List<TY_StatusCfgItem> getStatusCfg4StatusSchema(String StatusSchema) throws EX_ESMAPI, IOException
    {
        List<TY_StatusCfgItem> userStatusAssignments = null;
        if (StringUtils.hasText(StatusSchema) && StringUtils.hasText(srvCloudUrls.getStatusSchemaUrl()))
        {

            JsonNode jsonNode = null;
            HttpResponse response = null;
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            String urlLink = null;
            try
            {

                log.info("Fetching Details for Status Schema: " + StatusSchema);

                urlLink = srvCloudUrls.getStatusSchemaUrl() + StatusSchema;

                String encoding = Base64.getEncoder()
                        .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());

                HttpGet httpGet = new HttpGet(urlLink);

                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                httpGet.addHeader("accept", "application/json");

                // Fire the Url
                response = httpClient.execute(httpGet);

                // verify the valid error code first
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK)
                {
                    String msg = msgSrc.getMessage("ERR_INVALID_SCHEMA", new Object[]
                    { StatusSchema }, Locale.ENGLISH);
                    log.error(msg);
                    throw new EX_ESMAPI(msg);
                }

                // Try and Get Entity from Response
                HttpEntity entity = response.getEntity();
                String apiOutput = EntityUtils.toString(entity);
                // Lets see what we got from API
                // System.out.println(apiOutput);

                // Conerting to JSON
                ObjectMapper mapper = new ObjectMapper();
                jsonNode = mapper.readTree(apiOutput);

                if (jsonNode != null)
                {

                    JsonNode rootNode = jsonNode.path("value");
                    if (rootNode != null)
                    {
                        userStatusAssignments = new ArrayList<TY_StatusCfgItem>();

                        JsonNode contentNode = rootNode.at("/userStatusAssignments");
                        if (contentNode != null && contentNode.isArray() && contentNode.size() > 0)
                        {
                            log.info("Status for Schema : " + StatusSchema + " bound..");
                            for (JsonNode arrayItem : contentNode)
                            {
                                String userStatus = null, userStatusDescription = null;

                                Iterator<Entry<String, JsonNode>> fields = arrayItem.fields();
                                while (fields.hasNext())
                                {
                                    Entry<String, JsonNode> jsonField = fields.next();
                                    if (jsonField.getKey().equals("userStatus"))
                                    {
                                        userStatus = jsonField.getValue().asText();
                                    }

                                    if (jsonField.getKey().equals("userStatusDescription"))
                                    {
                                        userStatusDescription = jsonField.getValue().asText();
                                    }

                                }
                                userStatusAssignments.add(new TY_StatusCfgItem(userStatus, userStatusDescription));

                            }

                        }

                    }

                }
            }
            catch (Exception e)
            {
                throw new EX_ESMAPI(msgSrc.getMessage("ERR_INVALID_SCHEMA", new Object[]
                { StatusSchema, e.getMessage() }, Locale.ENGLISH));

            }
            finally
            {
                httpClient.close();
            }

        }

        return userStatusAssignments;
    }

    @Override
    public boolean updateCasewithReply(TY_CasePatchInfo patchInfo, TY_Case_SrvCloud_Reply caseReply)
            throws EX_ESMAPI, IOException
    {
        boolean caseUpdated = false;
        if (caseReply != null && patchInfo != null)
        {
            if (StringUtils.hasText(patchInfo.getCaseGuid()) && StringUtils.hasText(patchInfo.getETag()))
            {
                HttpClient httpclient = HttpClients.createDefault();
                String casePOSTURL = getPOSTURL4BaseUrl(srvCloudUrls.getCaseDetailsUrl());
                if (StringUtils.hasText(casePOSTURL))
                {
                    casePOSTURL = casePOSTURL + patchInfo.getCaseGuid();

                    String encoding = Base64.getEncoder()
                            .encodeToString((srvCloudUrls.getUserName() + ":" + srvCloudUrls.getPassword()).getBytes());
                    HttpPatch httpPatch = new HttpPatch(casePOSTURL);
                    httpPatch.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                    httpPatch.addHeader("Content-Type", "application/json");
                    httpPatch.addHeader(GC_Constants.gc_IFMatch, patchInfo.getETag());

                    // Remove Description Note Type from Payload before Persisting
                    // Important as the Description or Default text Type Should not be persisted
                    // alongwith Note(s)
                    if (CollectionUtils.isNotEmpty(caseReply.getNotes()))
                    {
                        caseReply.getNotes()
                                .removeIf(n -> n.getNoteType().equalsIgnoreCase(GC_Constants.gc_DescNoteType));
                    }

                    ObjectMapper objMapper = new ObjectMapper();

                    String requestBody = objMapper.writeValueAsString(caseReply);
                    log.info(requestBody);

                    StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    httpPatch.setEntity(entity);

                    // PATCH Case in Service Cloud
                    try
                    {
                        // Fire the Url
                        HttpResponse response = httpclient.execute(httpPatch);
                        // verify the valid error code first
                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode != HttpStatus.SC_OK)
                        {
                            HttpEntity entityResp = response.getEntity();
                            String apiOutput = EntityUtils.toString(entityResp);
                            log.error(apiOutput);
                            // Error Updating Case id - {0}. HTTP Status - {1}. Details : {2}.
                            throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_REPLY_UPDATE", new Object[]
                            { patchInfo.getCaseId(), statusCode, apiOutput }, Locale.ENGLISH));

                        }
                        else
                        {
                            caseUpdated = true;
                        }

                    }
                    catch (IOException e)
                    {
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_NOTES_POST", new Object[]
                        { e.getLocalizedMessage() }, Locale.ENGLISH));
                    }
                }

            }

        }
        return caseUpdated;
    }

    private String getPOSTURL4BaseUrl(String urlBase)
    {
        String url = null;
        if (StringUtils.hasText(urlBase))
        {
            String[] urlParts = urlBase.split("\\?");
            if (urlParts.length > 0)
            {
                url = urlParts[0];
            }
        }
        return url;
    }

}
