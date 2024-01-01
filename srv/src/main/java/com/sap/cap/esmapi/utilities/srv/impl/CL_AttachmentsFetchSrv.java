package com.sap.cap.esmapi.utilities.srv.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.pojos.TY_PreviousAttachments;
import com.sap.cap.esmapi.utilities.pojos.TY_SrvCloudUrls;
import com.sap.cap.esmapi.utilities.srv.intf.IF_AttachmentsFetchSrv;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CL_AttachmentsFetchSrv implements IF_AttachmentsFetchSrv
{

    private final TY_SrvCloudUrls srvCloudUrls;

    @Override
    public List<TY_PreviousAttachments> getAttachments4CaseByCaseGuid(String caseGuid) throws EX_ESMAPI
    {

        List<TY_PreviousAttachments> prevAtt = null;
        if (StringUtils.hasText(caseGuid) && StringUtils.hasText(srvCloudUrls.getDlAtt())
                && StringUtils.hasText(srvCloudUrls.getPrevAtt()))

        {
            
        }

        return prevAtt;
    }

}
