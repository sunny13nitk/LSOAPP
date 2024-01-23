package com.sap.cap.esmapi.events.event;

import org.springframework.context.ApplicationEvent;

import com.sap.cap.esmapi.ui.pojos.TY_CaseFormAsync;
import com.sap.cap.esmapi.utilities.srvCloudApi.destination.pojos.TY_DestinationProps;

import lombok.Getter;

@Getter
public class EV_CaseFormSubmit extends ApplicationEvent
{
    private TY_CaseFormAsync payload;

    private TY_DestinationProps desProps;

    public EV_CaseFormSubmit(Object source, TY_CaseFormAsync payload, TY_DestinationProps destinationProps)
    {
        super(source);
        this.payload = payload;
        this.desProps = destinationProps;
    }

}
