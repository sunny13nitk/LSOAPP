package com.sap.cap.esmapi.utilities.pojos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class PojoItem 
{
    private Map<String, List<String>> items = new HashMap<>();

    public Map<String, List<String>> getItems() 
    {
        return items;
    }

    @JsonAnySetter
    public void setItem(String key, List<String> values)
    {
        this.items.put(key, values);
    }    
}
