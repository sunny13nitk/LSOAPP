package com.sap.cap.esmapi.vhelps.srv.intf;

import java.util.Map;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

public interface IF_VHelpLOBUIModelSrv
{
    public Map<String, ?> getVHelpUIModelMap4LobCatg(EnumCaseTypes lob, String catgId) throws EX_ESMAPI;
}
