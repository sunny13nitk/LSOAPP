package com.sap.cap.esmapi.utilities.srv.intf;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.pojos.TY_UserESS;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContact;
import com.sap.cloud.security.xsuaa.token.Token;

import org.apache.http.HttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface IF_UserAPISrv
{

    public Ty_UserAccountContact getUserDetails(@AuthenticationPrincipal Token token) throws EX_ESMAPI;

    public String getAccountIdByUserEmail(String userEmail) throws EX_ESMAPI;

    public String getContactPersonIdByUserEmail(String userEmail) throws EX_ESMAPI;

    public TY_UserESS getESSDetails(@AuthenticationPrincipal Token token) throws EX_ESMAPI;

    public String createAccount() throws EX_ESMAPI;
    
}
