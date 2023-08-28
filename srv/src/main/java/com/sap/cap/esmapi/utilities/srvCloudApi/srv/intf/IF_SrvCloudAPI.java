package com.sap.cap.esmapi.utilities.srvCloudApi.srv.intf;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.sap.cap.esmapi.catg.pojos.TY_CatalogItem;
import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.status.pojos.TY_StatusCfgItem;
import com.sap.cap.esmapi.ui.pojos.TY_Attachment;
import com.sap.cap.esmapi.utilities.enums.EnumCaseTypes;
import com.sap.cap.esmapi.utilities.pojos.TY_AttachmentResponse;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseCatalogCustomizing;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseDetails;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseESS;
import com.sap.cap.esmapi.utilities.pojos.TY_CaseGuidId;
import com.sap.cap.esmapi.utilities.pojos.TY_Case_SrvCloud;
import com.sap.cap.esmapi.utilities.pojos.TY_NotesCreate;
import com.sap.cap.esmapi.utilities.pojos.Ty_UserAccountContactEmployee;
import com.sap.cap.esmapi.vhelps.pojos.TY_KeyValue;

/*
 * Impl with comments
 */
public interface IF_SrvCloudAPI
{
        public JsonNode getAllCases() throws IOException;

        public List<TY_CaseESS> getCases4User(String accountIdUser, String contactIdUser) throws IOException;

        public List<TY_CaseESS> getCases4User(Ty_UserAccountContactEmployee userDetails) throws IOException;

        public List<TY_CaseESS> getCases4User(Ty_UserAccountContactEmployee userDetails, EnumCaseTypes caseType)
                        throws IOException;

        public List<TY_CaseGuidId> getCaseGuidIdList();

        public Long getNumberofCases() throws IOException;

        public JsonNode getAllAccounts() throws IOException;

        public JsonNode getAllEmployees() throws IOException;

        public JsonNode getAllContacts() throws IOException;

        public String getAccountIdByUserEmail(String userEmail) throws EX_ESMAPI;

        public String getEmployeeIdByUserId(String userId) throws EX_ESMAPI;

        public String createCase(TY_Case_SrvCloud caseEntity) throws EX_ESMAPI;

        public String getContactPersonIdByUserEmail(String userEmail) throws EX_ESMAPI;

        public String createAccount(String userEmail, String userName) throws EX_ESMAPI;

        public String createNotes(TY_NotesCreate notes) throws EX_ESMAPI;

        public TY_AttachmentResponse createAttachment(TY_Attachment attachment) throws EX_ESMAPI;

        public boolean persistAttachment(String url, MultipartFile file) throws EX_ESMAPI, IOException;

        public TY_CaseCatalogCustomizing getActiveCaseTemplateConfig4CaseType(String caseType)
                        throws EX_ESMAPI, IOException;

        public List<TY_CatalogItem> getActiveCaseCategoriesByCatalogId(String catalogID) throws EX_ESMAPI, IOException;

        public List<TY_KeyValue> getVHelpDDLB4Field(String fieldName) throws EX_ESMAPI, IOException;

        public TY_CaseDetails getCaseDetails4Case(String caseId) throws EX_ESMAPI, IOException;

        public List<TY_StatusCfgItem> getStatusCfg4StatusSchema(String StatusSchema) throws EX_ESMAPI, IOException;

}
