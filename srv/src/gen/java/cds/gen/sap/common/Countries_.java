package cds.gen.sap.common;

import com.sap.cds.ql.CdsName;
import com.sap.cds.ql.ElementRef;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.cqn.CqnPredicate;
import java.lang.String;
import java.util.function.Function;
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
public interface Countries_ extends StructuredType<Countries_> {
  String CDS_NAME = "sap.common.Countries";

  ElementRef<String> name();

  ElementRef<String> descr();

  ElementRef<String> code();

  CountriesTexts_ texts();

  CountriesTexts_ texts(Function<CountriesTexts_, CqnPredicate> filter);

  CountriesTexts_ localized();

  CountriesTexts_ localized(Function<CountriesTexts_, CqnPredicate> filter);
}
