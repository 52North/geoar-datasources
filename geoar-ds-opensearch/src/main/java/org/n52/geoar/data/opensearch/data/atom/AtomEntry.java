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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.n52.geoar.data.opensearch.data.BaseFeedEntry;
import org.n52.geoar.data.opensearch.data.core.FeedElement;
import org.n52.geoar.data.opensearch.data.core.ICategory;
import org.n52.geoar.data.opensearch.data.core.IContent;
import org.n52.geoar.data.opensearch.data.core.IFeed;
import org.xml.sax.Attributes;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class AtomEntry extends BaseFeedEntry {
    //@formatter:off
    public static final String TITLE_ATTRIB         = "title";
    public static final String ID_ATTRIB            = "id";
    public static final String CONTENT_ATTRIB       = "content";
    public static final String SUMMARY_ATTRIB       = "summary";
    public static final String NAME_ATTRIB          = "name";
    public static final String AUTHOR_ATTRIB        = "author";
    public static final String CATEGORY_ATTRIB      = "category";
    public static final String UPDATED_ATTRIB       = "updated";
    public static final String LINK_ATTRIB          = "link";
    public static final String RIGHTS_ATTRIB        = "rights";
    
    public static final String BBOX_ATTRIB          = "bbox";
    //@formatter:on
    
    public AtomEntry(String name, String uri, Attributes attibutes) {
        super(name, uri, attibutes);
    }

    @Override
    public String getTitle() {
        FeedElement title = getElement(TITLE_ATTRIB);
        return title == null ? null : title.getContent();
    }

    @Override
    public String getId() {
        FeedElement id = getElement(ID_ATTRIB);
        return id == null ? null : id.getContent();
    }

    @Override
    public String getLink() {
        FeedElement link = getElement(LINK_ATTRIB);
        return link == null ? null : link.getContent();
    }

    @Override
    public List<String> getLinks() {
        FeedElement link = getElement(LINK_ATTRIB);
        return link == null ? null : Arrays.asList(link.getContent());
    }

    @Override
    public String getDescription() {
        FeedElement description = getElement(SUMMARY_ATTRIB);
        return description == null ? null : description.getContent();
    }

    @Override
    public String getContents() {
        FeedElement element = getElement(CONTENT_ATTRIB);
        return element == null ? null : element.getContent();
    }

    @Override
    public Date getPublisedDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getUpdatedDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<FeedElement> getCategorys() {
        return getElementList(CATEGORY_ATTRIB);
    }

    @Override
    public IFeed getRelatedFeed() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeedElement getAuthor() {
        return getElement(AUTHOR_ATTRIB);
    }

    @Override
    public FeedElement getBbox() {
        List<FeedElement> bbox = getElementList(BBOX_ATTRIB);
        return bbox == null ? null : bbox.get(0);
    }

    @Override
    public String getRights() {
        FeedElement element = getElement(RIGHTS_ATTRIB);
        return element == null ? null : element.getContent();
    }

}
