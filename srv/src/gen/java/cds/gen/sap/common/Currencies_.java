package cds.gen.sap.common;

import com.sap.cds.ql.CdsName;
import com.sap.cds.ql.ElementRef;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.cqn.CqnPredicate;
import java.lang.String;
import java.util.function.Function;
import javax.annotation.Generated;

/**
 * Code list for currencies
 *
 * See https://cap.cloud.sap/docs/cds/common#entity-sapcommoncurrencies
 */
@CdsName("sap.common.Currencies")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T10:04:38.092987600Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface Currencies_ extends StructuredType<Currencies_> {
  String CDS_NAME = "sap.common.Currencies";

  ElementRef<String> name();

  ElementRef<String> descr();

  ElementRef<String> code();

  ElementRef<String> symbol();

  CurrenciesTexts_ texts();

  CurrenciesTexts_ texts(Function<CurrenciesTexts_, CqnPredicate> filter);

  CurrenciesTexts_ localized();

  CurrenciesTexts_ localized(Function<CurrenciesTexts_, CqnPredicate> filter);
}
