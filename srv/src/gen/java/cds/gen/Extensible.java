package cds.gen;

import com.sap.cds.CdsData;
import com.sap.cds.Struct;
import com.sap.cds.ql.CdsName;
import java.lang.String;

/**
 * Aspects for extensible entities.
 */
@CdsName("extensible")
public interface Extensible extends CdsData {
  String EXTENSIONS_ = "extensions__";

  @CdsName(EXTENSIONS_)
  String getExtensions();

  @CdsName(EXTENSIONS_)
  void setExtensions(String extensions);

  static Extensible create() {
    return Struct.create(Extensible.class);
  }
}
