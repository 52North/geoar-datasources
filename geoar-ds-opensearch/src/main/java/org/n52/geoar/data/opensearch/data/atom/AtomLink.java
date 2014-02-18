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
import org.n52.geoar.data.opensearch.data.core.ILink;
import org.xml.sax.Attributes;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class AtomLink extends BaseFeedElement implements ILink {

    public static final String HREF = "href";
    public static final String HREFLANG = "hreflang";
    public static final String REL = "rel";
    public static final String TITLE = "title";
    public static final String TYPE = "type";

    public AtomLink(String name, String uri, Attributes attibutes) {
        super(name, uri, attibutes);
    }

    @Override
    public String getTitle() {
        FeedElement title = getElement(TITLE);
        return title == null ? null : title.getContent();
    }

    @Override
    public String getRel() {
        FeedElement rel = getElement(REL);
        return rel == null ? null : rel.getContent();
    }

    @Override
    public String getType() {
        FeedElement type = getElement(TYPE);
        return type == null ? null : type.getContent();
    }

    @Override
    public String getHref() {
        FeedElement element = getElement(HREF);
        return element == null ? null : element.getContent();
    }

    @Override
    public String getHrefLang() {
        FeedElement element = getElement(HREFLANG);
        return element == null ? null : element.getContent();
    }

}
