package com.sap.cap.esmapi.status.srv.intf;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.status.pojos.TY_StatusCfg;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

public interface IF_StatusSrv
{
    public TY_StatusCfg getStatusCfg4CaseType(EnumCaseTypes caseType) throws EX_ESMAPI;
}
