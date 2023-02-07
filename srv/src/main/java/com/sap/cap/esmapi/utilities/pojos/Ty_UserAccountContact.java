package com.sap.cap.esmapi.utilities.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ty_UserAccountContact
{
    private String userId;
    private String userEmail;
    private String accountId;
    private String contactId;    
}
