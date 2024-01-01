package com.sap.cap.esmapi.utilities.pojos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TY_PreviousAttachments
{
    private String id;
    private String title;
    private double fileSize;
    private String createdByName;
    private boolean byTechnicalUser;
    private String url;

}
