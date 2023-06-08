package com.sap.cap.esmapi.utilities.srv.intf;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.utilities.pojos.TY_UserDetails;
import com.sap.cap.esmapi.utilities.pojos.TY_UserSessionInfo;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContactEmployee;
import com.sap.cloud.security.xsuaa.token.Token;

public interface IF_UserSessionSrv
{

    /*
     * Get User Credentials via Token - Get and persist in Session if Not Bound -
     * Get only if Already bound for a session
     */
    public TY_UserDetails getUserDetails(@AuthenticationPrincipal Token token) throws EX_ESMAPI;

    /*
     * Get Complete User Session Info Details via Token - Get and Persist if not
     * bound - Get only if bound for session - if refresh true -- reload Cases and
     * Stats for Current User and refurbish in Session
     */
    public TY_UserSessionInfo getESSDetails(@AuthenticationPrincipal Token token, boolean refresh) throws EX_ESMAPI;

    /*
     * -- Submit Case Form : After comsumer Call to Rate Limit Pass --Validate Case
     * Form - Implicit Call ---- Fail - Messages{TY_Messages} :push in messagesStack
     * ---- Succ - Create and Publish Case Submit Event
     */
    public boolean SubmitCaseForm(TY_Case_Form caseForm);

    public String createAccount() throws EX_ESMAPI;

    public Ty_UserAccountContactEmployee getUserDetails4mSession();

    public void addSessionMessage(String msg);

    public List<String> getSessionMessages();

    public boolean isWithinRateLimit();

    public boolean isCaseFormValid();

    /*
     * Update Session Messages and Log(s) for a submission event result with Case Id
     * to notify user and update DB
     */
    public boolean updateCase4SubmissionId(String submGuid, String caseId, String msg) throws EX_ESMAPI;

}