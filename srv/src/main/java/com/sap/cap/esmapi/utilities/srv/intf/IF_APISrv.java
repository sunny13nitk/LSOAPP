package com.sap.cap.esmapi.utilities.srv.intf;

import java.io.IOException;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;

public interface IF_APISrv
{
    public long getNumberofEntitiesByUrl(String url) throws EX_ESMAPI, IOException ;
}
