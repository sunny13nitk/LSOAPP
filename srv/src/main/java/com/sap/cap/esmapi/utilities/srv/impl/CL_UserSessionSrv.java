package com.sap.cap.esmapi.utilities.srv.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.sap.cap.esmapi.events.event.EV_LogMessage;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_Case_Form;
import com.sap.cap.esmapi.ui.srv.intf.IF_ESS_UISrv;
import com.sap.cap.esmapi.utilities.enums.EnumMessageType;
import com.sap.cap.esmapi.utilities.enums.EnumStatus;
import com.sap.cap.esmapi.utilities.pojos.TY_Message;
import com.sap.cap.esmapi.utilities.pojos.TY_RLConfig;
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

    @Autowired
    private IF_ESS_UISrv essSrv;

    @Autowired
    private TY_RLConfig rlConfig;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
        // Token must be present
        if (token != null)
        {
            // get User Details with Token
            getUserDetails(token);

            // Reload Cases if Refresh Requested or Cases List Blank
            if (refresh || CollectionUtils.isEmpty(userSessInfo.getCases()))
            {
                if (userSessInfo.getUserDetails() != null)
                {
                    try
                    {
                        // Get the cases for User
                        userSessInfo.setCases(essSrv.getCases4User(userSessInfo.getUserDetails().getUsAcConEmpl()));
                        // Once the cases are in Get the Stats too
                        userSessInfo.setStats(essSrv.getStatsForUserCases(userSessInfo.getCases()));
                    }
                    catch (Exception e)
                    {
                        // Log error
                        log.error(msgSrc.getMessage("ERR_CASES_USER", new Object[]
                        { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(), e.getLocalizedMessage() },
                                Locale.ENGLISH));

                        // Raise Exception to be handled at UI via Central Aspect
                        throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASES_USER", new Object[]
                        { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(), e.getLocalizedMessage() },
                                Locale.ENGLISH));
                    }
                }
            }

        }

        return this.userSessInfo;
    }

    @Override
    public boolean SubmitCaseForm(TY_Case_Form caseForm)
    {
        boolean isSubmitted = true;

        return isSubmitted;
    }

    @Override
    public String createAccount() throws EX_ESMAPI
    {
        String accountId = null;
        // Only if no Account or Employee Identified in Current Session
        // No Account Determined
        if (!StringUtils.hasText(userSessInfo.getUserDetails().getUsAcConEmpl().getAccountId()))
        {
            // No Employee determined
            if (!StringUtils.hasText(userSessInfo.getUserDetails().getUsAcConEmpl().getEmployeeId()))
            {
                // Create new Individual Customer Account with User Credentials
                // User Email and UserName Bound
                if (StringUtils.hasText(userSessInfo.getUserDetails().getUsAcConEmpl().getUserEmail())
                        && StringUtils.hasText(userSessInfo.getUserDetails().getUsAcConEmpl().getUserName()))
                {

                    try
                    {
                        accountId = srvCloudApiSrv.createAccount(
                                userSessInfo.getUserDetails().getUsAcConEmpl().getUserEmail(),
                                userSessInfo.getUserDetails().getUsAcConEmpl().getUserName());
                        // Also update in the session for newly created Account
                        if (StringUtils.hasText(accountId))
                        {
                            userSessInfo.getUserDetails().getUsAcConEmpl().setAccountId(accountId);
                            // Session Display Message
                            this.addSessionMessage(msgSrc.getMessage("NEW_AC", new Object[]
                            { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId() }, Locale.ENGLISH));
                            // Add to Log
                            log.info(msgSrc.getMessage("NEW_AC", new Object[]
                            { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId() }, Locale.ENGLISH));

                            TY_Message message = new TY_Message(
                                    userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                    Timestamp.from(Instant.now()), EnumStatus.Success, EnumMessageType.SUCC_ACC_CREATE,
                                    accountId, msgSrc.getMessage("NEW_AC", new Object[]
                                    { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId() }, Locale.ENGLISH));
                            // For Logging Framework
                            userSessInfo.getMessagesStack().add(message);
                            // Instantiate and Fire the Event
                            EV_LogMessage logMsgEvent = new EV_LogMessage(this, message);
                            applicationEventPublisher.publishEvent(logMsgEvent);
                        }
                    }
                    catch (EX_ESMAPI ex) // Any Error During Individual Customer Creation for the User
                    {
                        log.error(msgSrc.getMessage("ERR_API_AC", new Object[]
                        { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId() }, Locale.ENGLISH));

                        TY_Message message = new TY_Message(userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                Timestamp.from(Instant.now()), EnumStatus.Error, EnumMessageType.ERR_ACC_CREATE,
                                userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                msgSrc.getMessage("ERR_API_AC", new Object[]
                                { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId() }, Locale.ENGLISH));
                        // For Logging Framework
                        userSessInfo.getMessagesStack().add(message);
                        // Instantiate and Fire the Event
                        EV_LogMessage logMsgEvent = new EV_LogMessage(this, message);
                        applicationEventPublisher.publishEvent(logMsgEvent);

                    }

                }

            }
        }

        return accountId;
    }

    @Override
    public Ty_UserAccountContactEmployee getUserDetails4mSession()
    {
        if (this.userSessInfo.getUserDetails() != null)
        {
            return userSessInfo.getUserDetails().getUsAcConEmpl();
        }

        return null;
    }

    @Override
    public void addSessionMessage(String msg)
    {
        if (StringUtils.hasText(msg))
        {
            userSessInfo.getMessages().add(msg);
        }
    }

    @Override
    public List<String> getSessionMessages()
    {
        return userSessInfo.getMessages();
    }

    @Override
    public boolean isWithinRateLimit()
    {

        boolean withinRateLimit = true;

        // Rate Config Specified
        if (rlConfig != null)
        {
            if (CollectionUtils.isNotEmpty(userSessInfo.getFormSubmissions().getFormSubmissions()))
            {
                // Current # Submissions more than or equals to # configurable - check
                if (userSessInfo.getFormSubmissions().getFormSubmissions().size() > rlConfig.getNumFormSubms())
                {
                    // Get Current Time Stamp
                    Timestamp currTS = Timestamp.from(Instant.now());
                    // Get Top N :latest Submissions since submissions are always appended
                    // chronologically
                    List<Timestamp> topNSubmList = new ArrayList<Timestamp>();
                    topNSubmList = userSessInfo.getFormSubmissions().getFormSubmissions();
                    Collections.reverse(topNSubmList);

                    // Compare the Time difference from the latest one
                    long secsElapsedLastSubmit = (currTS.getTime() - topNSubmList.get(0).getTime()) / 1000;
                    // Last Submission elapsed time less than
                    if (secsElapsedLastSubmit < rlConfig.getIntvSecs())
                    {
                        withinRateLimit = false;
                        log.error(msgSrc.getMessage("ERR_RATE_LIMIT", new Object[]
                        { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(currTS) }, Locale.ENGLISH));

                        TY_Message logMsg = new TY_Message(userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                currTS, EnumStatus.Error, EnumMessageType.ERR_RATELIMIT,
                                userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                msgSrc.getMessage("ERR_RATE_LIMIT", new Object[]
                                { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                        new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(currTS) }, Locale.ENGLISH));
                        // For Logging Framework
                        userSessInfo.getMessagesStack().add(logMsg);
                        // Instantiate and Fire the Event
                        EV_LogMessage logMsgEvent = new EV_LogMessage(this, logMsg);
                        applicationEventPublisher.publishEvent(logMsgEvent);

                    }
                    else
                    {
                        // Clear the older Submissions Details - Since now the clock is refreshed for
                        // Current Session after Waiting
                        userSessInfo.getFormSubmissions().getFormSubmissions().clear();
                        withinRateLimit = true;
                        userSessInfo.getFormSubmissions().getFormSubmissions().add(currTS);
                    }

                }
                else // Just add the Form Submission time Stamp to Session
                {
                    userSessInfo.getFormSubmissions().getFormSubmissions().add(Timestamp.from(Instant.now()));
                    withinRateLimit = true;
                }

            }

        }

        return withinRateLimit;
    }

    @Override
    public boolean isCaseFormValid()
    {
        boolean isValid = true;

        // Get the Latest Form Submission from Session and Validate
        if (userSessInfo.getCurrentForm4Submission() != null)
        {
            if (userSessInfo.getCurrentForm4Submission().getCaseForm() != null)
            {
                // Subject and Category are Mandatory fields
                if (!StringUtils.hasText(userSessInfo.getCurrentForm4Submission().getCaseForm().getSubject())
                        || !StringUtils.hasText(userSessInfo.getCurrentForm4Submission().getCaseForm().getCatgDesc()))
                {
                    // Payload error
                    log.error(msgSrc.getMessage("ERR_CASE_PAYLOAD", new Object[]
                    { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                            new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Timestamp.from(Instant.now())) },
                            Locale.ENGLISH));

                    TY_Message message = new TY_Message(userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                            Timestamp.from(Instant.now()), EnumStatus.Error, EnumMessageType.ERR_PAYLOAD,
                            userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                            msgSrc.getMessage("ERR_CASE_PAYLOAD", new Object[]
                            { userSessInfo.getUserDetails().getUsAcConEmpl().getUserId(),
                                    new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Timestamp.from(Instant.now())) },
                                    Locale.ENGLISH));

                    // For Logging Framework
                    userSessInfo.getMessagesStack().add(message);
                    // Instantiate and Fire the Event
                    EV_LogMessage logMsgEvent = new EV_LogMessage(this, message);
                    applicationEventPublisher.publishEvent(logMsgEvent);

                    isValid = false;
                }

                // Also to Include Country mandatory check for certain category if requested by
                // business.

                // Also check for Allowed Attachment Type(s) if provided by the business
            }
        }

        return isValid;
    }

    @Override
    public boolean updateCase4SubmissionId(String submGuid, String caseId, String msg) throws EX_ESMAPI
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCase4SubmissionId'");
    }

}
