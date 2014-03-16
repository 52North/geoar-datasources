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
package org.n52.geoar.data.opensearch.data.atom;

import org.n52.geoar.data.opensearch.data.BaseFeedElement;
import org.n52.geoar.data.opensearch.data.core.FeedElement;
import org.n52.geoar.data.opensearch.data.core.ICategory;
import org.xml.sax.Attributes;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class AtomCategory extends BaseFeedElement implements ICategory{
    private static final String LABEL = "label";
    private static final String TERM = "term";
    private static final String SCHEME = "scheme";
    
    public AtomCategory(String name, String uri, Attributes attibutes) {
        super(name, uri, attibutes);
    }

    @Override
    public String getTerm() {
        FeedElement term = getElement(TERM);
        return term == null ? null : term.getContent();
    }

    @Override
    public String getLabel() {
        FeedElement term = getElement(LABEL);
        return term == null ? null : term.getContent();
    }

    @Override
    public String getScheme() {
        FeedElement scheme = getElement(SCHEME);
        return scheme == null ? null : scheme.getContent();
    }
}
