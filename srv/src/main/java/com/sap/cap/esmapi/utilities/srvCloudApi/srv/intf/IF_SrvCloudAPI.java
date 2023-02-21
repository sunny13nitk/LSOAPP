package com.sap.cap.esmapi.utilities.srvCloudApi.srv.intf;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseESS;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseGuidId;

public interface IF_SrvCloudAPI
{
    public JsonNode getAllCases() throws IOException;    

    public List<TY_CaseESS> getCases4User(String accountIdUser, String contactIdUser)throws IOException;

    public List<TY_CaseGuidId> getCaseGuidIdList();

    public Long getNumberofCases() throws IOException;

    public JsonNode getAllAccounts() throws IOException;

    public JsonNode getAllContacts() throws IOException;

    public String getAccountIdByUserEmail(String userEmail) throws EX_ESMAPI;

    public String getContactPersonIdByUserEmail(String userEmail) throws EX_ESMAPI;

    public String createAccount(String userEmail, String userName) throws EX_ESMAPI;
    
}
