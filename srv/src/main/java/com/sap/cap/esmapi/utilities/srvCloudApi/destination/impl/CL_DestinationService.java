package com.sap.cap.esmapi.utilities.srvCloudApi.destination.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.hana.logging.srv.intf.IF_HANALoggingSrv;
import com.sap.cap.esmapi.utilities.enums.EnumMessageType;
import com.sap.cap.esmapi.utilities.enums.EnumStatus;
import com.sap.cap.esmapi.utilities.pojos.TY_Message;
import com.sap.cap.esmapi.utilities.srv.intf.IF_UserSessionSrv;
import com.sap.cap.esmapi.utilities.srvCloudApi.destination.intf.IF_DestinationService;
import com.sap.cap.esmapi.utilities.srvCloudApi.destination.pojos.TY_DestinationProps;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@SessionScope
@RequiredArgsConstructor
@Slf4j
public class CL_DestinationService implements IF_DestinationService
{

    private final IF_HANALoggingSrv logSrv;

    private final MessageSource msgSrc;

    private final IF_UserSessionSrv userSessSrv;

    private TY_DestinationProps destinationProps;

    private static final String prop_URL = "URL";
    private static final String prop_Token = "authTokens";
    private static final String cons_value = ", value=";
    private static final String cons_bracketClose = "\\)";

    @Override
    public TY_DestinationProps getDestinationDetails4Destination(String destinationName) throws EX_ESMAPI
    {

        if (StringUtils.hasText(destinationName))
        {
            if (this.destinationProps == null)
            {
                getDestinationDetails(destinationName);
            }

        }

        return this.destinationProps;
    }

    private void getDestinationDetails(String destinationName)
    {
        try
        {

            log.info("Scanning for Destination : " + destinationName);
            Destination dest = DestinationAccessor.getDestination(destinationName);
            if (dest != null)
            {

                log.info("Destination Bound via Destination Accessor.");

                destinationProps = new TY_DestinationProps();

                for (String prop : dest.getPropertyNames())
                {

                    if (prop.equals(prop_URL))
                    {
                        destinationProps.setBaseUrl(dest.get(prop).get().toString());
                    }

                    if (prop.equals(prop_Token))
                    {
                        destinationProps.setAuthToken(parseToken(dest.get(prop).get().toString()));
                    }

                }

            }
        }
        catch (DestinationAccessException e)
        {
            log.error("Error Accessing Destination : " + e.getLocalizedMessage());
            String msg = msgSrc.getMessage("ERR_DESTINATION_ACCESS", new Object[]
            { destinationName, e.getLocalizedMessage() }, Locale.ENGLISH);
            if (logSrv != null)
            {
                logSrv.createLog(new TY_Message(userSessSrv.getUserDetails4mSession().getUserName(),
                        Timestamp.from(Instant.now()), EnumStatus.Error, EnumMessageType.ERR_SRVCLOUDAPI,
                        destinationName, msg));

            }

        }
    }

    private String parseToken(String authToken)
    {
        String token = null;

        if (StringUtils.hasText(authToken))
        {
            String[] tokens = authToken.split(cons_value);
            if (tokens.length > 0)
            {
                String tokenval = tokens[tokens.length -1];
                if (StringUtils.hasText(tokenval))
                {
                    String[] tokenAuth = tokenval.split(cons_bracketClose);
                    if(tokenAuth.length > 0)
                    {
                        token = tokenAuth[0];
                    }
                }
            }
        }

        return token;
    }

}
