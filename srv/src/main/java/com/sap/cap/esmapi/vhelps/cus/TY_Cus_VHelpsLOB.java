package com.sap.cap.esmapi.vhelps.cus;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TY_Cus_VHelpsLOB
{
    @JsonProperty("LOB")
    public String LOB;
    @JsonProperty("fieldNames")
    public List<String> fieldNames;
}
