package com.sap.cap.esmapi.ui.pojos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TY_CaseFormAsync
{
    private TY_Case_Form caseForm;
    private String emailId;
    private String submGuid;
    private List<String> roles = new ArrayList<String>();
    private Timestamp timestamp;
    private boolean authenticated;

}
