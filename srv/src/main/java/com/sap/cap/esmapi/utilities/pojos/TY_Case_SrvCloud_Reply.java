package com.sap.cap.esmapi.utilities.pojos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TY_Case_SrvCloud_Reply
{
    private String caseType;
    private String status;
    private List<TY_CaseReplyNote> notes;
    private List<TY_Attachment_CaseCreate> attachments;

}
