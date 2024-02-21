package cds.gen.sap.common;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Code list for countries
 *
 * See https://cap.cloud.sap/docs/cds/common#entity-countries
 */
@CdsName("sap.common.Countries")
@Generated(
    value = "cds-maven-plugin",
<<<<<<< HEAD
    date = "2024-02-21T11:05:15.432736508Z",
=======
    date = "2024-02-21T11:08:17.533507400Z",
>>>>>>> 0987c49 (pom version changes)
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Countries extends CdsData {
  String NAME = "name";

  String DESCR = "descr";

  String CODE = "code";

  String TEXTS = "texts";

  String LOCALIZED = "localized";

  String getName();

  void setName(String name);

  String getDescr();

  void setDescr(String descr);

  String getCode();

  void setCode(String code);

  List<CountriesTexts> getTexts();

  void setTexts(List<? extends Map<String, ?>> texts);

  CountriesTexts getLocalized();

  void setLocalized(Map<String, ?> localized);

  Countries_ ref();

  static Countries create() {
    return Struct.create(Countries.class);
  }

  static Countries create(String code) {
    Map<String, Object> keys = new HashMap<>();
    keys.put(CODE, code);
    return Struct.access(keys).as(Countries.class);
  }
}
