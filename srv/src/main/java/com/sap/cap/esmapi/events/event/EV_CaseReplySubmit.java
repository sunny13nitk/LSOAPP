package com.sap.cap.esmapi.events.event;

import org.springframework.context.ApplicationEvent;

import com.sap.cap.esmapi.ui.pojos.TY_CaseEditFormAsync;
import com.sap.cap.esmapi.utilities.srvCloudApi.destination.pojos.TY_DestinationProps;

import lombok.Getter;

@Getter
public class EV_CaseReplySubmit extends ApplicationEvent
{
    private TY_CaseEditFormAsync payload;

    private TY_DestinationProps desProps;

    public EV_CaseReplySubmit(Object source, TY_CaseEditFormAsync payload, TY_DestinationProps destinationProps)
    {
        super(source);
        this.payload = payload;
        this.desProps = destinationProps;
    }

}
