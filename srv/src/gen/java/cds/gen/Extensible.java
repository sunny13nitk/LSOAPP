package cds.gen;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import javax.annotation.Generated;

/**
 * Aspects for extensible entities.
 */
@CdsName("extensible")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T11:05:15.432736508Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Extensible extends CdsData {
  String EXTENSIONS_ = "extensions__";

  @CdsName(EXTENSIONS_)
  String getExtensions();

  @CdsName(EXTENSIONS_)
  void setExtensions(String extensions);

  static Extensible create() {
    return Struct.create(Extensible.class);
  }
}
