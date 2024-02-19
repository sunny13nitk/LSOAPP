package cds.gen.sap.common;

import com.sap.cds.ql.CdsName;
import com.sap.cds.ql.ElementRef;
import com.sap.cds.ql.StructuredType;
import java.lang.String;
import javax.annotation.Generated;

@CdsName("sap.common.Languages.texts")
@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-19T17:43:27.367465Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
public interface LanguagesTexts_ extends StructuredType<LanguagesTexts_> {
  String CDS_NAME = "sap.common.Languages.texts";

  ElementRef<String> locale();

  ElementRef<String> name();

  ElementRef<String> descr();

  ElementRef<String> code();
}
