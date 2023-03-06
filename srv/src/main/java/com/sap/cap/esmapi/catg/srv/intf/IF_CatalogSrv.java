package com.sap.cap.esmapi.catg.srv.intf;

import java.util.List;

import com.sap.cap.esmapi.catg.pojos.TY_CatalogTree;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

public interface IF_CatalogSrv 
{
    public List<TY_CatalogTree> getCaseCatgTree4LoB(EnumCaseTypes caseType)  throws EX_ESMAPI;    
}
