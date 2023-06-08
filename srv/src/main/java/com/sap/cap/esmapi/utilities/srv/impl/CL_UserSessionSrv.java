package com.sap.cap.esmapi.utilities.srv.impl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.utilities.pojos.TY_UserDetails;
import com.sap.cap.esmapi.utilities.pojos.TY_UserSessionInfo;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContactEmployee;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserSessionSrv;
import com.sap.cap.esmapi.utilities.srvCloudApi.srv.intf.IF_SrvCloudAPI;
import com.sap.cds.services.request.UserInfo;
import com.sap.cloud.security.xsuaa.token.Token;

import lombok.extern.slf4j.Slf4j;

@Service
@SessionScope
@Slf4j
public class CL_UserSessionSrv implements IF_UserSessionSrv
{

    @Autowired
    private MessageSource msgSrc;

    @Autowired
    private UserInfo userInfo;

    @Autowired
    private IF_SrvCloudAPI srvCloudApiSrv;

    // Properties

    private TY_UserSessionInfo userSessInfo;

    @Override
    public TY_UserDetails getUserDetails(Token token) throws EX_ESMAPI
    {
        // Token Blank
        if (token == null)
        {
            log.error(msgSrc.getMessage("NO_TOKEN", null, Locale.ENGLISH));
            throw new EX_ESMAPI(msgSrc.getMessage("NO_TOKEN", null, Locale.ENGLISH));

        }
        else
        {
            // Unauthenticated User
            if (!userInfo.isAuthenticated())
            {
                log.error(msgSrc.getMessage("UNAUTHENTICATED_ACCESS", new Object[]
                { token.getLogonName() }, Locale.ENGLISH));
                throw new EX_ESMAPI(msgSrc.getMessage("UNAUTHENTICATED_ACCESS", new Object[]
                { token.getLogonName() }, Locale.ENGLISH));
            }

            else
            {

                // Role Checks to be explicitly handled here
                if (CollectionUtils.isNotEmpty(userInfo.getRoles()))
                {
                    // Explicit Role Check for Interals and Externals and error in case of
                    // unassigned Role
                }

                // Return from Session if Populated else make some effort
                if (userSessInfo.getUserDetails() == null)
                {
                    // Fetch and Return
                    TY_UserDetails userDetails = new TY_UserDetails();
                    userDetails.setAuthenticated(true);
                    userDetails.setRoles(userInfo.getRoles().stream().collect(Collectors.toList()));
                    Ty_UserAccountContactEmployee usAccConEmpl = new Ty_UserAccountContactEmployee();
                    usAccConEmpl.setUserId(token.getLogonName());
                    usAccConEmpl.setUserName(token.getGivenName() + " " + token.getFamilyName());
                    usAccConEmpl.setUserEmail(token.getEmail());
                    usAccConEmpl.setAccountId(srvCloudApiSrv.getAccountIdByUserEmail(usAccConEmpl.getUserEmail()));
                    usAccConEmpl
                            .setContactId(srvCloudApiSrv.getContactPersonIdByUserEmail(usAccConEmpl.getUserEmail()));

                    // Only seek Employee If Account/Contact not Found
                    if (!StringUtils.hasText(usAccConEmpl.getAccountId()))
                    {
                        // Seek Employee and populate
                        usAccConEmpl.setEmployeeId(srvCloudApiSrv.getEmployeeIdByUserId(usAccConEmpl.getUserId()));
                        usAccConEmpl.setEmployee(true);

                    }
                    userDetails.setUsAcConEmpl(usAccConEmpl);
                    userSessInfo.setUserDetails(userDetails); // Set in Session

                }
            }

        }

        return userSessInfo.getUserDetails();
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
