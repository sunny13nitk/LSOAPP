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
 * Code list for languages
 *
 * See https://cap.cloud.sap/docs/cds/common#entity-languages
 */
@CdsName("sap.common.Languages")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-19T17:43:27.367465Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Languages extends CdsData {
  String NAME = "name";

  String DESCR = "descr";

  String CODE = "code";

  String TEXTS = "texts";

  String LOCALIZED = "localized";

  String getName();

  void setName(String name);

  String getDescr();

  void setDescr(String descr);

  /**
   * Type for a language code
   */
  String getCode();

  /**
   * Type for a language code
   */
  void setCode(String code);

  List<LanguagesTexts> getTexts();

  void setTexts(List<? extends Map<String, ?>> texts);

  LanguagesTexts getLocalized();

  void setLocalized(Map<String, ?> localized);

  Languages_ ref();

  static Languages create() {
    return Struct.create(Languages.class);
  }

  static Languages create(String code) {
    Map<String, Object> keys = new HashMap<>();
    keys.put(CODE, code);
    return Struct.access(keys).as(Languages.class);
  }
}
