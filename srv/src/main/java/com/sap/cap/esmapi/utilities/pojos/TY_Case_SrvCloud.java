package com.sap.cap.esmapi.utilities.pojos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TY_Case_SrvCloud
{
    private String subject;    
    private String caseType;
    private TY_Account_CaseCreate account;
    private TY_CatgLvl1_CaseCreate categoryLevel1;
    private TY_CatgLvl1_CaseCreate categoryLevel2;
    private TY_CatgLvl1_CaseCreate categoryLevel3;
    private TY_CatgLvl1_CaseCreate categoryLevel4;
    private TY_Description_CaseCreate description;
    @Override
    public String toString()
    {
        return "TY_Case_SrvCloud [subject=" + subject + ", caseType=" + caseType + ", account=" + account
                + ", categoryLevel1=" + categoryLevel1 + ", categoryLevel2=" + categoryLevel2 + ", categoryLevel3="
                + categoryLevel3 + ", categoryLevel4=" + categoryLevel4 + ", description=" + description + "]";
    }
    
    

    

}
