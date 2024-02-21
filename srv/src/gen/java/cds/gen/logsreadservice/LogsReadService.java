package cds.gen.logsreadservice;

import com.sap.cds.ql.CdsName;
import com.sap.cds.services.cds.ApplicationService;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.cds.RemoteService;
import javax.annotation.processing.Generated;

@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T10:47:07.638874300Z",
    comments = "com.sap.cds:cds-maven-plugin:2.6.1 / com.sap.cds:cds4j-api:2.6.1"
)
@CdsName(LogsReadService_.CDS_NAME)
public interface LogsReadService extends CqnService {
  interface Application extends ApplicationService, LogsReadService {
  }

  interface Remote extends RemoteService, LogsReadService {
  }
}
