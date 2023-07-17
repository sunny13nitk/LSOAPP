package com.sap.cap.esmapi.catg.srv.intf;

import com.sap.cap.esmapi.catg.pojos.TY_CatalogTree;
import com.sap.cap.esmapi.catg.pojos.TY_CatgTemplates;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

public interface IF_CatalogSrv 
{
    public TY_CatalogTree getCaseCatgTree4LoB(EnumCaseTypes caseType)  throws EX_ESMAPI;
    
    public String[] getCatgHierarchyforCatId(String catId, EnumCaseTypes caseType) throws EX_ESMAPI;

    public TY_CatgTemplates getTemplates4Catg(String catId,EnumCaseTypes caseType)  throws EX_ESMAPI;
}
