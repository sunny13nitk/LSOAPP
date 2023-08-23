package com.sap.cap.esmapi.utilities.pojos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TY_CaseDetails
{
    private String caseId;
    private String caseGuid;
    private String origin;
    private String status;

    private List<TY_NotesDetails> notes;

}
