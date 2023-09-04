package com.sap.cap.esmapi.status.srv.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.status.pojos.TY_PortalStatusTransI;
import com.sap.cap.esmapi.status.pojos.TY_PortalStatusTransitions;
import com.sap.cap.esmapi.status.pojos.TY_StatusCfg;
import com.sap.cap.esmapi.status.srv.intf.IF_StatusSrv;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@SessionScope
@RequiredArgsConstructor
@Slf4j
public class CL_StatusSrv implements IF_StatusSrv
{

    private final TY_CatgCus catgCus; // Autowired

    private final TY_PortalStatusTransitions statusTransitions; // Autowired

    private final MessageSource msgSrc; // Autowired

    private List<TY_StatusCfg> lobStatusCfgList;

    @Override
    public TY_StatusCfg getStatusCfg4CaseType(EnumCaseTypes caseType) throws EX_ESMAPI
    {
        TY_StatusCfg statusCFG = null;
        if (caseType != null && catgCus != null)
        {
            if (CollectionUtils.isNotEmpty(lobStatusCfgList))
            {
                Optional<TY_StatusCfg> statusCfgO = lobStatusCfgList.stream()
                        .filter(s -> s.getCaseType().equals(caseType)).findFirst();
                if (statusCfgO.isPresent())
                {
                    statusCFG = statusCfgO.get();
                }
                else
                {
                    statusCFG = fetchStatusCfg4CaseType(caseType);
                }
            }
            else
            {
                statusCFG = fetchStatusCfg4CaseType(caseType);
            }
        }

        return statusCFG;
    }

    @Override
    public TY_PortalStatusTransI getPortalStatusTransition4CaseTypeandCaseStatus(String caseType, String caseStatus)
            throws EX_ESMAPI
    {
        TY_PortalStatusTransI statTransCus = null;

        if (StringUtils.hasText(caseStatus) && StringUtils.hasText(caseType) && msgSrc != null)
        {
            if (CollectionUtils.isNotEmpty(statusTransitions.getStatusTransitions()))
            {
                Optional<TY_PortalStatusTransI> transO = statusTransitions.getStatusTransitions().stream().filter(s ->
                {
                    if (s.getCaseType().equals(caseType) && s.getFromStatus().equalsIgnoreCase(caseStatus))
                    {
                        return true;
                    }
                    return false;
                }).findFirst();
                if (transO.isPresent())
                {
                    statTransCus = transO.get();
                }
                else
                {
                    String msg = msgSrc.getMessage("ERR_CFG_STATTRAN_NOTFOUND", new Object[]
                    { caseType, caseStatus }, Locale.ENGLISH);
                    log.error(msg);

                    throw new EX_ESMAPI(msg);
                }
            }
        }

        return statTransCus;
    }

    private TY_StatusCfg fetchStatusCfg4CaseType(EnumCaseTypes caseType)
    {

        if (CollectionUtils.isNotEmpty(catgCus.getCustomizations()))
        {
            Optional<TY_CatgCusItem> cusO = catgCus.getCustomizations().stream()
                    .filter(c -> c.getCaseTypeEnum().name().equalsIgnoreCase(caseType.name())).findFirst();
            if (cusO.isPresent())
            {
                TY_CatgCusItem cus = cusO.get();
                if (StringUtils.hasText(cus.getStatusSchema()))
                {

                }
            }
        }

        return null;
    }

}
