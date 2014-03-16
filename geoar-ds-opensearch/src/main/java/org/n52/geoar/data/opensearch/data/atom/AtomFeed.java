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

import java.util.Date;
import java.util.List;

import org.n52.geoar.data.opensearch.data.BaseFeedElement;
import org.n52.geoar.data.opensearch.data.FeedDateTimeConverter;
import org.n52.geoar.data.opensearch.data.core.FeedElement;
import org.n52.geoar.data.opensearch.data.core.ICategory;
import org.n52.geoar.data.opensearch.data.core.IFeed;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class AtomFeed extends BaseFeedElement implements IFeed {

    public static final String TITLE_ELEMENT = "title";
    public static final String SUBTITLE_ELEMENT = "subtitle";
    public static final String UPDATED_ELEMENT = "updated";
    public static final String LINK_ELEMENT = "link";
    public static final String RIGHTS_ELEMENT = "rights";
    public static final String CATEGORY_ELEMENT = "category";
    public static final String ENTRY_ELEMENT = "entry";

    public static final String NAMESPACE_ATOM = "http://www.w3.org/2005/Atom";

    public AtomFeed(String name, String uri, Attributes attributes) {
        super(name, uri, attributes);
    }

    @Override
    public String getTitle() {
        FeedElement title = getElement(TITLE_ELEMENT);
        return title == null ? null : title.getContent();
    }

    @Override
    public String getFeedType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        FeedElement description = getElement(SUBTITLE_ELEMENT);
        return description == null ? null : description.getContent();
    }

    @Override
    public Date getPublishedDate() {
        FeedElement publishedDate = getElement(UPDATED_ELEMENT);
        return FeedDateTimeConverter.parseW3CDate(publishedDate.getContent());
    }

    @Override
    public List<FeedElement> getCategories() {
        return getElementList(CATEGORY_ELEMENT);
    }

    @Override
    public List<FeedElement> getEntrys() {
        return getElementList(ENTRY_ELEMENT);
    }
}
