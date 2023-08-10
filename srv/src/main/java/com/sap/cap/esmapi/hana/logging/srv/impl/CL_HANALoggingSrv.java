package com.sap.cap.esmapi.hana.logging.srv.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;
import com.sap.cap.esmapi.hana.logging.srv.intf.IF_HANALoggingSrv;
import com.sap.cap.esmapi.utilities.pojos.TY_Message;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.services.persistence.PersistenceService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CL_HANALoggingSrv implements IF_HANALoggingSrv
{

    @Autowired
    private PersistenceService ps;

    @Autowired
    private MessageSource msgSrc;

    private final String msgLogsTablePath = "db.esmlogs.esmappmsglog"; // Table Path - HANA

    @Override
    public void createLog(TY_Message logMsg) throws EX_ESMAPI
    {
        if (logMsg != null && msgSrc != null && ps != null)
        {
            String msg = msgSrc.getMessage("PERS_LOG", new Object[]
            { logMsg.getUserName(), logMsg.getMsgType().toString() }, Locale.ENGLISH);
            if (StringUtils.hasText(msg))
            {
                log.info(msg);
            }

            Map<String, Object> logEntity = new HashMap<String, Object>();
            logEntity.put("ID", UUID.randomUUID()); // ID
            logEntity.put("username", logMsg.getUserName()); // User Name
            logEntity.put("timestamp", new Timestamp(System.currentTimeMillis())); // TimeStamp
            logEntity.put("status", logMsg.getStatus().toString()); // Status
            logEntity.put("msgtype", logMsg.getMsgType().toString()); // Message Type
            logEntity.put("objectid", logMsg.getObjectId()); // Object ID
            logEntity.put("message", logMsg.getMessage()); // Message Text

            CqnInsert qLogInsert = Insert.into(this.msgLogsTablePath).entry(logEntity);
            if (qLogInsert != null)
            {
                log.info("LOG Insert Query Bound!");
                Result result = ps.run(qLogInsert);
                if (result.list().size() > 0)
                {
                    log.info("Log Successfully Inserted! " + result.rowCount());
                }
            }
        }
    }

}
