package cds.gen.db.esmlogs;

import com.sap.cds.ql.CdsName;
import com.sap.cds.ql.ElementRef;
import com.sap.cds.ql.StructuredType;
import java.lang.String;
import java.time.Instant;
import javax.annotation.Generated;

/**
 * Aspect for entities with canonical universal IDs
 *
 * See https://cap.cloud.sap/docs/cds/common#aspect-cuid
 */
@CdsName("db.esmlogs.esmappmsglog")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T10:04:38.092987600Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Esmappmsglog_ extends StructuredType<Esmappmsglog_> {
  String CDS_NAME = "db.esmlogs.esmappmsglog";

  ElementRef<String> ID();

  ElementRef<String> username();

  ElementRef<Instant> timestamp();

  ElementRef<String> status();

  ElementRef<String> msgtype();

  ElementRef<String> objectid();

  ElementRef<String> message();
}
