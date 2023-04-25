package com.sap.cap.esmapi.ui.pojos;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TY_Case_Form 
{
    private String accId;
    private String caseTxnType;
    private String catgDesc;
    private String subject;
    private String description;
    private MultipartFile attachment;
    @Override
    public String toString() {
        return "TY_Case_Form [accId=" + accId + ", caseTxnType=" + caseTxnType + ", catgDesc=" + catgDesc
                + ", description=" + description + ", subject=" + subject + "]";
    }

    
    
}
