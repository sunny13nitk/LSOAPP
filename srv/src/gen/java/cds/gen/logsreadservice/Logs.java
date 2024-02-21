package cds.gen.logsreadservice;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Aspect for entities with canonical universal IDs
 *
 * See https://cap.cloud.sap/docs/cds/common#aspect-cuid
 */
@CdsName("LogsReadService.Logs")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T09:29:56.857963511Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Logs extends CdsData {
  String ID = "ID";

  String USERNAME = "username";

  String TIMESTAMP = "timestamp";

  String STATUS = "status";

  String MSGTYPE = "msgtype";

  String OBJECTID = "objectid";

  String MESSAGE = "message";

  @CdsName(ID)
  String getId();

  @CdsName(ID)
  void setId(String id);

  String getUsername();

  void setUsername(String username);

  Instant getTimestamp();

  void setTimestamp(Instant timestamp);

  String getStatus();

  void setStatus(String status);

  String getMsgtype();

  void setMsgtype(String msgtype);

  String getObjectid();

  void setObjectid(String objectid);

  String getMessage();

  void setMessage(String message);

  Logs_ ref();

  static Logs create() {
    return Struct.create(Logs.class);
  }

  static Logs create(String id) {
    Map<String, Object> keys = new HashMap<>();
    keys.put(ID, id);
    return Struct.access(keys).as(Logs.class);
  }
}
