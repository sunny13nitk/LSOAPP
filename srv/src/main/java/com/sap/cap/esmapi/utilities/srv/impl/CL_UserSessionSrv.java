package com.sap.cap.esmapi.utilities.srv.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.utilities.pojos.TY_UserDetails;
import com.sap.cap.esmapi.utilities.pojos.TY_UserSessionInfo;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContactEmployee;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserSessionSrv;
import com.sap.cloud.security.xsuaa.token.Token;

@Service
@SessionScope
public class CL_UserSessionSrv implements IF_UserSessionSrv
{

    @Override
    public TY_UserDetails getUserDetails(Token token) throws EX_ESMAPI
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserDetails'");
    }

    @Override
    public TY_UserSessionInfo getESSDetails(Token token, boolean refresh) throws EX_ESMAPI
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getESSDetails'");
    }

    @Override
    public boolean SubmitCaseForm(TY_Case_Form caseForm)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'SubmitCaseForm'");
    }

    @Override
    public String createAccount() throws EX_ESMAPI
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAccount'");
    }

    @Override
    public Ty_UserAccountContactEmployee getUserDetails4mSession()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserDetails4mSession'");
    }

    @Override
    public void addSessionMessage(String msg)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addSessionMessage'");
    }

    @Override
    public List<String> getSessionMessages()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSessionMessages'");
    }

    @Override
    public boolean isWithinRateLimit()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isWithinRateLimit'");
    }

    @Override
    public boolean isCaseFormValid()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCaseFormValid'");
    }

    @Override
    public boolean updateCase4SubmissionId(String submGuid, String caseId, String msg) throws EX_ESMAPI
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCase4SubmissionId'");
    }

}
