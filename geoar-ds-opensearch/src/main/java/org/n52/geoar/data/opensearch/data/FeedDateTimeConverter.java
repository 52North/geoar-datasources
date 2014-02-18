/**
 * Copyright 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.geoar.data.opensearch.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class FeedDateTimeConverter {

    private static final DateFormat ATOM_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    
    //TODO finish this!
    public static Date parseW3CDate(String date) {
        int indexT = date.indexOf("T");
        if (indexT > -1) {
            date = date.endsWith("Z") ? date.substring(0, date.length() - 1)
                    + "+00:00" : date;
            int indexTZ = date.indexOf("+", indexT);
            indexTZ = indexTZ == -1 ? date.indexOf("-", indexT) : indexTZ;
            
            if(indexTZ >-1){
                String p = date.substring(0, indexTZ);
                int secFraction = p.indexOf(",");
                if(secFraction>-1){
                    p = p.substring(0, secFraction);
                }
                String post = date.substring(indexTZ);
                date = p + "GMT" + post;
            }
        }
        else{
            date += "T00:00GMT";
        }
        
        return null;
    }
}
