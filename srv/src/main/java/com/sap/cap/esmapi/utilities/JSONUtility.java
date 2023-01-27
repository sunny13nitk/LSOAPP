package com.sap.cap.esmapi.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtility
{

    public static List<String> getKeysInJsonUsingJsonNodeFieldNames
    (String json, ObjectMapper mapper) throws JsonMappingException, JsonProcessingException 
    {

        List<String> keys = new ArrayList<>();
        JsonNode jsonNode = mapper.readTree(json);
        Iterator<String> iterator = jsonNode.fieldNames();
        iterator.forEachRemaining(e -> keys.add(e));
        return keys;
    }
    
}
