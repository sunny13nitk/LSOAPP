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
    private boolean external;
    @Override
    public String toString()
    {
        return "Ty_UserAccountContactEmployee [userId=" + userId + ", userName=" + userName + ", userEmail=" + userEmail
                + ", accountId=" + accountId + ", contactId=" + contactId + ", employeeId=" + employeeId + ", employee="
                + employee + ", external=" + external + "]";
    }

    
}
