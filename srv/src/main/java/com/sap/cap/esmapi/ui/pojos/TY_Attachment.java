package com.sap.cap.esmapi.ui.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TY_Attachment
{
    private String fileName;
    private String category;
    private boolean isSelected;
}
