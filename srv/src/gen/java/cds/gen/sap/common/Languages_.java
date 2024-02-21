package cds.gen.sap.common;

import com.sap.cds.ql.CdsName;
import com.sap.cds.ql.ElementRef;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.cqn.CqnPredicate;
import java.lang.String;
import java.util.function.Function;
import javax.annotation.Generated;

/**
 * Code list for languages
 *
 * See https://cap.cloud.sap/docs/cds/common#entity-sapcommonlanguages
 */
@CdsName("sap.common.Languages")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T10:04:38.092987600Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Languages_ extends StructuredType<Languages_> {
  String CDS_NAME = "sap.common.Languages";

  ElementRef<String> name();

  ElementRef<String> descr();

  ElementRef<String> code();

  LanguagesTexts_ texts();

  LanguagesTexts_ texts(Function<LanguagesTexts_, CqnPredicate> filter);

  LanguagesTexts_ localized();

  LanguagesTexts_ localized(Function<LanguagesTexts_, CqnPredicate> filter);
}
