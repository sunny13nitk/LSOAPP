package com.sap.cap.esmapi.hana.logging.srv.intf;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.pojos.TY_Message;

public interface IF_HANALoggingSrv
{
    //Create Log from Message POJO
    public void createLog(TY_Message logMsg) throws EX_ESMAPI;
}
