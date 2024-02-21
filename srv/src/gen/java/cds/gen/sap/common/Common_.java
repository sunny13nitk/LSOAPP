package cds.gen.sap.common;

import com.sap.cds.ql.CdsName;
import java.lang.Class;
import java.lang.String;
import javax.annotation.Generated;

@Generated(
    value = "cds-maven-plugin",
    date = "2024-02-21T08:10:52.891382693Z",
    comments = "com.sap.cds:cds-maven-plugin:1.30.2 / com.sap.cds:cds4j-api:1.34.2"
)
@CdsName("sap.common")
public interface Common_ {
  String CDS_NAME = "sap.common";

  Class<Languages_> LANGUAGES = Languages_.class;

  Class<CurrenciesTexts_> CURRENCIES_TEXTS = CurrenciesTexts_.class;

  Class<Countries_> COUNTRIES = Countries_.class;

  Class<CountriesTexts_> COUNTRIES_TEXTS = CountriesTexts_.class;

  Class<Currencies_> CURRENCIES = Currencies_.class;

  Class<LanguagesTexts_> LANGUAGES_TEXTS = LanguagesTexts_.class;
}
