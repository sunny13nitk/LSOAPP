package cds.gen;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import javax.annotation.Generated;

/**
 * Aspect for entities with canonical universal IDs
 *
 * See https://cap.cloud.sap/docs/cds/common#aspect-cuid
 */
@CdsName("cuid")
@Generated(
    value = "cds-maven-plugin",
<<<<<<< HEAD
    date = "2024-02-21T11:05:15.432736508Z",
=======
    date = "2024-02-21T11:08:17.533507400Z",
>>>>>>> 0987c49 (pom version changes)
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Cuid extends CdsData {
  String ID = "ID";

  @CdsName(ID)
  String getId();

  @CdsName(ID)
  void setId(String id);

  static Cuid create() {
    return Struct.create(Cuid.class);
  }
}
