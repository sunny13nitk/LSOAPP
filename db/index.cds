using {cuid} from '@sap/cds/common';

namespace db.model;


/*
--- Cannot be inserted Directly via OData - Managed via Logging Event
*/
@Capabilities.Insertable: false
@Capabilities.Deletable : false
@Capabilities.Updatable : false
entity esmappmsglog : cuid {
    username  : String(50);
    timestamp : Timestamp;
    status    : String(50);
    msgtype   : String(50);
    objectid  : String(50);
    message   : String(800);
}
