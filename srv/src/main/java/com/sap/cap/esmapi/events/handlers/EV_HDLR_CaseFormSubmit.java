package com.sap.cap.esmapi.events.handlers;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sap.cap.esmapi.events.event.EV_CaseFormSubmit;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EV_HDLR_CaseFormSubmit
{
    @Async
    @EventListener
    public void handleCaseFormSubmission(EV_CaseFormSubmit evCaseFormSubmit)
    {
        if(evCaseFormSubmit != null)
        {
            log.info("Inside Case Form Submit Event ----");
        }
    }
}
