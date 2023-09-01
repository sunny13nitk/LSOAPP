package com.sap.cap.esmapi.status.srv.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.SessionScope;

import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.status.pojos.TY_PortalStatusTransitions;
import com.sap.cap.esmapi.status.pojos.TY_StatusCfg;
import com.sap.cap.esmapi.status.srv.intf.IF_StatusSrv;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

import lombok.RequiredArgsConstructor;

@Service
@SessionScope
@RequiredArgsConstructor
public class CL_StatusSrv implements IF_StatusSrv
{

    private final TY_CatgCus catgCus; // Autowired

    private final TY_PortalStatusTransitions statusTransitions; // Autowired

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
    public boolean isEditAllowed4CaseStatus(String caseStatus) throws EX_ESMAPI
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEditAllowed4CaseStatus'");
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
