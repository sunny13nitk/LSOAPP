package com.sap.cap.esmapi.utilities.pojos;

import com.sap.cap.esmapi.utilities.enums.EnumMessageType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TY_Message
{
    private EnumMessageType msgType;

    private String message;
}
