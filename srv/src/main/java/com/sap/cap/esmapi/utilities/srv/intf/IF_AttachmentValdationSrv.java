package com.sap.cap.esmapi.utilities.srv.intf;

import org.springframework.web.multipart.MultipartFile;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;

public interface IF_AttachmentValdationSrv
{
    public boolean isValidAttachmentByName(MultipartFile attachment) throws EX_ESMAPI;
}
