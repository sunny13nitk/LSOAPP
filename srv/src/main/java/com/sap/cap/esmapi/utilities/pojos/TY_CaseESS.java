package com.sap.cap.esmapi.utilities.pojos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TY_CaseESS 
{
    private String guid;
    private String id;
    private String subject;
    private String statusDesc;
    private String createdOn;
    private Date creationDate;    
}
