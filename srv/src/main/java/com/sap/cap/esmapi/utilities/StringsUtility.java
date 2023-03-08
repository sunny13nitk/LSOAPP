package com.sap.cap.esmapi.utilities;



import org.springframework.util.StringUtils;

import com.sap.cap.esmapi.exceptions.EX_ESMAPI;

public class StringsUtility
{
    public static String replaceURLwithParams(String baseString, String[] replStrings, String separator) 
    {
        if(StringUtils.hasText(baseString))
        {
            //Check the Occurences and REplacements Count
            int countOccurances = org.apache.commons.lang3.StringUtils.countMatches(baseString, separator);
            int countREpl = replStrings.length;

            //Substitute all occurences with 1st repl
            if(countOccurances > countREpl)
            {
                if(countREpl == 1)
                {
                    baseString.replaceAll(separator, replStrings[0]);
                }
                else
                {
                    throw new EX_ESMAPI("Occurences to Replace - " + countOccurances +  " in String - " + baseString +" : "+ " are more that replacement values : " + countREpl)  ; 
                }
            }
            else if( countOccurances < countREpl)
            {
                throw new EX_ESMAPI("Occurences to Replace - " + countOccurances +  " in String - " + baseString +" : "+ " are less that replacement values : " + countREpl)  ; 
                
            }
            else  // Occurences = Replacements
            {
                int idx = 0, itr =0;
                while ((idx = baseString.indexOf(separator, idx)) != -1 )
                {
                    baseString.replaceFirst(separator, replStrings[itr]);
                    itr++;
                }

            }


            
        }

        return baseString;
    }    
}
