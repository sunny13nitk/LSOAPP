package cds.gen.sap.common;

import com.sap.cds.ql.CdsName;
import com.sap.cds.ql.ElementRef;
import com.sap.cds.ql.StructuredType;
import java.lang.String;
import javax.annotation.processing.Generated;

@CdsName("sap.common.Currencies.texts")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T10:47:07.638874300Z",
    comments = "com.sap.cds:cds-maven-plugin:2.6.1 / com.sap.cds:cds4j-api:2.6.1"
)
public interface CurrenciesTexts_ extends StructuredType<CurrenciesTexts_> {
  String CDS_NAME = "sap.common.Currencies.texts";

  ElementRef<String> locale();

  ElementRef<String> name();

  ElementRef<String> descr();

  ElementRef<String> code();
}
