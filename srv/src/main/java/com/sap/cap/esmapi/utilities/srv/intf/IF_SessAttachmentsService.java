package com.sap.cap.esmapi.utilities.srv.intf;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;

public interface IF_SessAttachmentsService
{
    // Initialize the Session Attachments container - Clear Messages too
    public void initialize();

    // Add Attachent from MultiPart Form Control
    public boolean addAttachment(MultipartFile file) throws EX_ESMAPI;

    // REmove Attachent by File Name
    public boolean removeAttachmentByName(String fileName);

    public boolean checkIFExists(String fileName);

    // Get All Session messages
    public List<String> getSessionMessages();
}
