package com.sap.cap.esmapi.catg.srv.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sap.cap.esmapi.catg.pojos.TY_CatalogItem;
import com.sap.cap.esmapi.catg.pojos.TY_CatalogTree;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCus;
import com.sap.cap.esmapi.catg.pojos.TY_CatgCusItem;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatalogSrv;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseCatalogCustomizing;
import com.sap.cap.esmapi.utilities.srvCloudApi.srv.intf.IF_SrvCloudAPI;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CL_CatalogSrv implements IF_CatalogSrv
{

    private List<TY_CatalogTree> caseCatgContainer;

    @Autowired
    private TY_CatgCus catgCus;

    @Autowired
    private IF_SrvCloudAPI srvCloudApiSrv;

    @Autowired
    private MessageSource msgSrc;

    private static final int maxCatgLevels = 4;

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
                }
                else
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

    @Override
    public String[] getCatgHierarchyforCatId(String catId, EnumCaseTypes caseType) throws EX_ESMAPI
    {
        String[] catTree = null;
        int idx = 0;

        if (StringUtils.hasText(catId) && caseType != null)
        {
            String catCurr = catId;

            // Get Complete Catalog Details
            TY_CatalogTree catalogTree = this.getCaseCatgTree4LoB(caseType);
            if (CollectionUtils.isNotEmpty(catalogTree.getCategories()))
            {
                catTree = new String[maxCatgLevels]; // Max upto 4 levels
                while (StringUtils.hasText(catCurr))
                {
                    String catScan = catCurr;
                    // Scan for Category in Catalog Tree
                    Optional<TY_CatalogItem> itemSel = catalogTree.getCategories().stream()
                            .filter(t -> t.getId().equals(catScan)).findFirst();
                    if (itemSel.isPresent())
                    {
                        catTree[idx] = catCurr;

                        // Seek Parent
                        if (StringUtils.hasText(itemSel.get().getParentId()))
                        {
                            catCurr = itemSel.get().getParentId();
                        }
                        else
                        {
                            catCurr = null;
                        }

                        idx++;
                    }

                }
            }
        }

        return catTree;
    }

    private TY_CatalogTree loadCatgTree4CaseType(EnumCaseTypes caseType)
    {

        TY_CatalogTree caseCatgTree = null;

        // Get the Config
        Optional<TY_CatgCusItem> caseCFgO = catgCus.getCustomizations().stream()
                .filter(g -> g.getCaseTypeEnum().toString().equals(caseType.toString())).findFirst();
        if (caseCFgO.isPresent() && srvCloudApiSrv != null)
        {
            // Read FRom Srv Cloud the Catg. Tree
            try
            {
                // Get config from Srv Cloud for Case type - Active Catalog ID
                TY_CaseCatalogCustomizing caseCus = srvCloudApiSrv
                        .getActiveCaseTemplateConfig4CaseType(caseCFgO.get().getCaseType());
                if (caseCus != null)
                {
                    if (StringUtils.hasText(caseCus.getCataglogId()))
                    {
                        // Get category Tree for Catalog ID
                        caseCatgTree = new TY_CatalogTree(caseType,
                                srvCloudApiSrv.getActiveCaseCategoriesByCatalogId(caseCus.getCataglogId()));
                        if (CollectionUtils.isNotEmpty(caseCatgTree.getCategories()))
                        {
                            // add to Container - for subsequent calls
                            if (caseCatgContainer == null)
                            {
                                caseCatgContainer = new ArrayList<TY_CatalogTree>();
                            }
                            this.caseCatgContainer.add(caseCatgTree);
                        }

                    }
                }

            }
            catch (Exception e)
            {
                throw new EX_ESMAPI(msgSrc.getMessage("ERR_CATG_LOAD", new Object[]
                { caseCFgO.get().getCatgCsvPath(), caseType.toString() }, Locale.ENGLISH));
            }

        }

        else
        {
            throw new EX_ESMAPI(msgSrc.getMessage("ERR_CASE_TYPE_NOCFG", new Object[]
            { caseType.toString() }, Locale.ENGLISH));
        }

        if(CollectionUtils.isNotEmpty(caseCatgTree.getCategories()))
        {
            caseCatgTree.getCategories().add(0, new TY_CatalogItem());
        }
        return caseCatgTree;
    }

}
