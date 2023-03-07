package com.sap.cap.esmapi.catg.srv.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.sap.cap.esmapi.catg.pojos.TY_CatalogTree;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatalogSrv;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CL_CatalogSrv implements IF_CatalogSrv {

    private List<TY_CatalogTree> caseCatgContainer;

    @Autowired
    private TY_CatgCus catgCus;

    @Autowired
    private MessageSource msgSrc;

    @Override
    public TY_CatalogTree getCaseCatgTree4LoB(EnumCaseTypes caseType) throws EX_ESMAPI
     {
        TY_CatalogTree caseCatgTree = null;
        if (caseType != null) 
        {
            if (!CollectionUtils.isEmpty(caseCatgContainer)) 
            {
                // 1. Check from Session if Loaded already!
                Optional<TY_CatalogTree> caseCatgTreeO = caseCatgContainer.stream()
                        .filter(f -> f.getCaseTypeEnum().toString().equals(caseType.toString())).findFirst();
                if (caseCatgTreeO.isPresent()) 
                {
                    System.out.println("REading Catg. Tree from Session for :" + caseType);
                    return caseCatgTreeO.get();
                } else 
                {
                    caseCatgTree = loadCatgTree4CaseType(caseType);
                }
            }
            else 
            {
                caseCatgTree = loadCatgTree4CaseType(caseType);
            }

        }

        return caseCatgTree;

    }

    private TY_CatalogTree loadCatgTree4CaseType(EnumCaseTypes caseType)
    {

        TY_CatalogTree caseCatgTree= null;


         // Get the Config
         Optional<TY_CatgCusItem> caseCFgO = catgCus.getCustomizations().stream()
         .filter(g -> g.getCaseTypeEnum().toString().equals(caseType.toString())).findFirst();
        if (caseCFgO.isPresent()) 
        {
            // Read FRom Srv Cloud the Catg. Tree
            try
            {

            }
            catch (Exception e)
            {
                throw new EX_ESMAPI(msgSrc.getMessage("ERR_CATG_LOAD",
                        new Object[] { caseCFgO.get().getCatgCsvPath(), caseType.toString() }, Locale.ENGLISH));
            }

        }

        else 
        {
            throw new EX_ESMAPI(
                    msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[] { caseType.toString() }, Locale.ENGLISH));
        }
        
        return caseCatgTree;
    }

}
