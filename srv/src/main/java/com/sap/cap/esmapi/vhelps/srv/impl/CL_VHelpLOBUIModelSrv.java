package com.sap.cap.esmapi.vhelps.srv.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sap.cap.esmapi.catg.pojos.TY_CatgDetails;
import com.sap.cap.esmapi.catg.srv.intf.IF_CatalogSrv;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.vhelps.cus.TY_Cus_VHelpsLOB;
import com.sap.cap.esmapi.vhelps.cus.TY_FieldProperties;
import com.sap.cap.esmapi.vhelps.cus.TY_VHelpsRoot;
import com.sap.cap.esmapi.vhelps.srv.intf.IF_VHelpLOBUIModelSrv;
import com.sap.cap.esmapi.vhelps.srv.intf.IF_VHelpSrv;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CL_VHelpLOBUIModelSrv implements IF_VHelpLOBUIModelSrv
{

    @Autowired
    private IF_CatalogSrv catalogSrv;

    @Autowired
    private IF_VHelpSrv vHelpSrv;

    @Autowired
    private TY_VHelpsRoot vHelpCusSrv;

    @Autowired
    private ApplicationContext appCtxt;

    @Override
    public Map<String, ?> getVHelpUIModelMap4LobCatg(EnumCaseTypes lob, String catgId) throws EX_ESMAPI
    {
        Map<String, ?> modelAttrs = new HashMap<String, Object>();

        if (StringUtils.hasText(lob.name()) && StringUtils.hasText(catgId) && catalogSrv != null && vHelpSrv != null
                && vHelpCusSrv != null)
        {

            // Get all RElevant Value help Fields for LoB
            if (CollectionUtils.isNotEmpty(vHelpCusSrv.getVHelpsCus()))
            {
                // Get the Category Description for the Category ID from Case Form
                TY_CatgDetails catgDetails = catalogSrv.getCategoryDetails4Catg(catgId, lob, true);
                // Get Customizing for LOB
                Optional<TY_Cus_VHelpsLOB> lobVHelpCusO = vHelpCusSrv.getVHelpsCus().stream()
                        .filter(x -> x.getLOB().equals(lob.name())).findFirst();

                if (lobVHelpCusO.isPresent())
                {
                    TY_Cus_VHelpsLOB lobVHelpCus = lobVHelpCusO.get();
                    // Iterate over fieldNames for Value Helps
                    for (TY_FieldProperties fldLob : lobVHelpCus.getFields())
                    {
                        if (StringUtils.hasText(fldLob.getFieldName()) && StringUtils.hasText(fldLob.getCatgListBean()))
                        {
                            // If Category specific - SCan for Category to match and Then Populate in
                            // Attributes Map
                            if (fldLob.isCatgSpecific())
                            {
                                if (StringUtils.hasText(catgDetails.getCatDesc()))
                                {

                                }
                            }

                            // Not Category specific - Get Vhelp and Populate in Attributes Map
                            else
                            {

                            }
                        }
                    }

                }

            }

        }

        return modelAttrs;

    }

}
