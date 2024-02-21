package cds.gen.sap.common;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import javax.annotation.Generated;

/**
 * Aspect for a code list with name and description
 *
 * See https://cap.cloud.sap/docs/cds/common#aspect-codelist
 */
@CdsName("sap.common.CodeList")
@Generated(
    value = "cds-maven-plugin",
<<<<<<< HEAD
    date = "2024-02-21T11:05:15.432736508Z",
=======
    date = "2024-02-21T11:08:17.533507400Z",
>>>>>>> 0987c49 (pom version changes)
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface CodeList extends CdsData {
  String NAME = "name";

  String DESCR = "descr";

  String getName();

  void setName(String name);

  String getDescr();

  void setDescr(String descr);

  static CodeList create() {
    return Struct.create(CodeList.class);
  }
}
