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

import java.util.Stack;

import org.n52.geoar.data.opensearch.data.atom.AtomEntry;
import org.n52.geoar.data.opensearch.data.atom.AtomFeed;
import org.n52.geoar.data.opensearch.data.core.IFeed;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class FeedHandler extends DefaultHandler {
    public static final String FEED_TAG = "feed";
    public static final String ENTRY_TAG = "entry";

    private IFeed feed;
    private StringBuilder builder;
    private Stack<BaseFeedElement> elementStack;

    @Override
    public void startDocument() throws SAXException {
        elementStack = new Stack<BaseFeedElement>();
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        BaseFeedElement newElement;

        if (localName.equalsIgnoreCase(FEED_TAG)) {
            this.feed = new AtomFeed(uri, FEED_TAG, attributes);
            newElement = (BaseFeedElement) this.feed;
        } else if (localName.equalsIgnoreCase(ENTRY_TAG)) {
            newElement = new AtomEntry(FEED_TAG, uri, attributes);
        } else {
            newElement = new BaseFeedElement(localName, uri, attributes);
        }

        elementStack.push(newElement);
        builder = new StringBuilder();
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        elementStack.clear();
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        BaseFeedElement current = elementStack.pop().setContent(builder.toString());
        
        if (!elementStack.empty())
            elementStack.peek().addElementToMap(localName, current);
        
        this.builder.delete(0, builder.length());
    }
    
    public IFeed getFeed(){
        return this.feed;
    }
}
