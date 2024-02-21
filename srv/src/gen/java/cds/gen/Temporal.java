package cds.gen;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import java.time.Instant;
import javax.annotation.Generated;

/**
 * Aspect for entities with temporal data
 *
 * See https://cap.cloud.sap/docs/cds/common#aspect-temporal
 */
@CdsName("temporal")
@Generated(
    value = "cds-maven-plugin",
<<<<<<< HEAD
    date = "2024-02-21T11:05:15.432736508Z",
=======
    date = "2024-02-21T11:08:17.533507400Z",
>>>>>>> 0987c49 (pom version changes)
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Temporal extends CdsData {
  String VALID_FROM = "validFrom";

  String VALID_TO = "validTo";

  Instant getValidFrom();

  void setValidFrom(Instant validFrom);

  Instant getValidTo();

  void setValidTo(Instant validTo);

  static Temporal create() {
    return Struct.create(Temporal.class);
  }
}
