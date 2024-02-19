package cds.gen.sap.common;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;
import javax.annotation.Generated;

@CdsName("sap.common.Languages.texts")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-19T17:43:27.367465Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface LanguagesTexts extends CdsData {
  String LOCALE = "locale";

  String NAME = "name";

  String DESCR = "descr";

  String CODE = "code";

  /**
   * Type for a language code
   */
  String getLocale();

  /**
   * Type for a language code
   */
  void setLocale(String locale);

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

  LanguagesTexts_ ref();

  static LanguagesTexts create() {
    return Struct.create(LanguagesTexts.class);
  }
}
