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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.geoar.data.opensearch.data.core.FeedElement;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class BaseFeedElement implements FeedElement{
    public static final int ATOM_TYPE = 1;
    
    private final String name;
    private final String uri;
    private String content;
    
    private final Attributes attributes;
    private final Map<String, List<FeedElement>> elementMap;

    public BaseFeedElement(String name, String uri){
        this.elementMap = new HashMap<String, List<FeedElement>>();
        this.attributes = new AttributesImpl();
        this.name = name;
        this.uri = uri;
    }
    
    public BaseFeedElement(String name, String uri, Attributes attibutes){
        this.elementMap = new HashMap<String, List<FeedElement>>();
        this.attributes = new AttributesImpl(attibutes);
        this.name = name;
        this.uri = uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getContent() {
        return content;
    }
    
    BaseFeedElement setContent(String content){
        this.content = content;
        return this;
    }

    @Override
    public FeedElement getElement(String key) {
        List<FeedElement> elements = elementMap.get(key);
        return elements == null ? null : elements.get(0);
    }

    @Override
    public List<FeedElement> getElementList(String key) {
        return elementMap.get(key);
    }

    @Override
    public Set<String> getElementKeys() {
        return elementMap.keySet();
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }
    
    void addElementToMap(String key, FeedElement element){
        if(elementMap.containsKey(key)){
            elementMap.get(key).add(element);
        } else {
            List<FeedElement> elementList = new ArrayList<FeedElement>();
            elementList.add(element);
            elementMap.put(key, elementList);
        }
    }

}
