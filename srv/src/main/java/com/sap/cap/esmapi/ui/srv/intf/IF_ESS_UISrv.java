package com.sap.cap.esmapi.ui.srv.intf;

import java.util.List;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.ui.pojos.TY_ESS_Stats;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseESS;

public interface IF_ESS_UISrv
{
    public TY_ESS_Stats getStatsForUserCases(List<TY_CaseESS> cases4User) throws EX_ESMAPI;    
}
