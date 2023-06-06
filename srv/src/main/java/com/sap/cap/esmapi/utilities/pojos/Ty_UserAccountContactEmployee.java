package com.sap.cap.esmapi.utilities.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ty_UserAccountContactEmployee
{
    private String userId;
    private String userName;
    private String userEmail;
    private String accountId;
    private String contactId;
    private String employeeId;
    private boolean employee;
}
