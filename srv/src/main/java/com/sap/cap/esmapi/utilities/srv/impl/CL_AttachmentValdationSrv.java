package com.sap.cap.esmapi.utilities.srv.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.pojos.TY_RLConfig;
import com.sap.cap.esmapi.utilities.srv.intf.IF_AttachmentValdationSrv;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CL_AttachmentValdationSrv implements IF_AttachmentValdationSrv
{

    private final TY_RLConfig rlConfig; // Constructor Injection

    @Override
    public boolean isValidAttachmentByName(MultipartFile attachment) throws EX_ESMAPI
    {
        boolean isValid = false;
        if (rlConfig != null && attachment != null)
        {
            if (StringUtils.hasText(rlConfig.getAllowedAttachments())
                    && StringUtils.hasText(attachment.getOriginalFilename()))
            {
                List<String> allowedAttachmentTypes = Arrays.asList(rlConfig.getAllowedAttachments().split("\\|"));
                if (CollectionUtils.isNotEmpty(allowedAttachmentTypes))
                {
                    // Get the Extension Type for Attachment
                    String filename = attachment.getOriginalFilename();
                    String[] fNameSplits = filename.split("\\.");

                    if (fNameSplits != null)
                    {
                        String extensionAttachment = null;
                        if (fNameSplits.length >= 1)
                        {
                            extensionAttachment = fNameSplits[fNameSplits.length - 1];
                        }
                        if (StringUtils.hasText(extensionAttachment))
                        {
                            String extnType = extensionAttachment;
                            Optional<String> extnfoundO = allowedAttachmentTypes.stream()
                                    .filter(a -> a.equalsIgnoreCase(extnType)).findFirst();
                            if (extnfoundO.isPresent())
                            {
                                // Valid Attachment TYpe
                                isValid = true;
                            }
                        }
                    }
                }
            }
        }

        return isValid;
    }

}
