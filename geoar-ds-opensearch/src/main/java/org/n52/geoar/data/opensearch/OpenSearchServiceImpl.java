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
package org.n52.geoar.data.opensearch;

import java.util.Map;

import org.n52.geoar.data.opensearch.data.descriptiondocument.OpenSearchDescriptionDocument;
import org.w3c.dom.Document;

public class OpenSearchServiceImpl implements OpenSearchService{
    
    private final Document inputDocument;
    private OpenSearchDescriptionDocument d;
    
    public OpenSearchServiceImpl(Document inputDocument){
        this.inputDocument = inputDocument;
        d = new OpenSearchDescriptionDocument(inputDocument);
    }

    @Override
    public Document getDescriptionDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getParamters() {
        // TODO Auto-generated method stub
        return null;
    }

}
