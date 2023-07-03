package com.sap.cap.esmapi.utilities.pojos;

import java.util.ArrayList;
import java.util.List;

import com.sap.cap.esmapi.ui.pojos.TY_CaseFormAsync;
import com.sap.cap.esmapi.ui.pojos.TY_ESS_Stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TY_UserSessionInfo
{
    private TY_UserDetails userDetails;
    private TY_FormSubmissions formSubmissions;
    private TY_CaseFormAsync currentForm4Submission;
    private List<String> messages ; // Cases ESS List Messages only - New Cases Created
    private List<TY_CaseESS> cases; // Every Roundtrip Refresh - Event Case Form Submit/Status Change TBD
    private List<TY_Message> messagesStack = new ArrayList<TY_Message>(); // Session Messages Flow Stack
    private String formErrorMsg;
}
