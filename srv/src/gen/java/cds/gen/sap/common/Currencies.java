package cds.gen.sap.common;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.Short;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Code list for currencies
 *
 * See https://cap.cloud.sap/docs/cds/common#entity-currencies
 */
@CdsName("sap.common.Currencies")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T11:05:15.432736508Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Currencies extends CdsData {
  String NAME = "name";

  String DESCR = "descr";

  String CODE = "code";

  String SYMBOL = "symbol";

  String MINOR_UNIT = "minorUnit";

  String TEXTS = "texts";

  String LOCALIZED = "localized";

  String getName();

  void setName(String name);

  String getDescr();

  void setDescr(String descr);

  String getCode();

  void setCode(String code);

  String getSymbol();

  void setSymbol(String symbol);

  Short getMinorUnit();

  void setMinorUnit(Short minorUnit);

  List<CurrenciesTexts> getTexts();

  void setTexts(List<? extends Map<String, ?>> texts);

  CurrenciesTexts getLocalized();

  void setLocalized(Map<String, ?> localized);

  Currencies_ ref();

  static Currencies create() {
    return Struct.create(Currencies.class);
  }

  static Currencies create(String code) {
    Map<String, Object> keys = new HashMap<>();
    keys.put(CODE, code);
    return Struct.access(keys).as(Currencies.class);
  }
}
