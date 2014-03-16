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
package org.n52.geoar.data.opensearch.data.descriptiondocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.geoar.data.opensearch.OpenSearchConstants;
import org.n52.geoar.data.opensearch.data.exception.ElementNodeNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class OpenSearchDescriptionDocument {

    public static final String ROOT_NODE = "OpenSearchDescription";
    public static final String TAG_SHORTNAME = "ShortName";
    public static final String TAG_DESCRIPTION = "Description";
    public static final String TAG_URL = "Url";
    public static final String TAG_QUERY = "Query";
    public static final String TAG_CONTACT = "Contact";
    public static final String TAG_TAGS = "Tags";
    public static final String TAG_LONGNAME = "LongName";
    public static final String TAG_DEVELOPER = "Developer";

    public static final String TAG_ADULTCONTENT = "AdultContent";
    public static final String TAG_SYNDICATIONRIGHT = "SyndicationRight";
    public static final String TAG_INPUTENCODING = "inputEncoding";
    public static final String TAG_OUTPUTENCODING = "outputEncoding";
    public static final String TAG_IMAGE = "Image";
    public static final String TAG_LANGUAGE = "Language";
    public static final String TAG_ATTRIBUTION = "Attribution";

    public static interface SyndicationRight {
        public static final int OPEN = 0;
        public static final int LIMITED = 1;
        public static final int PRIVATE = 2;
        public static final int CLOSED = 3;
    }

    protected String shortName;
    protected String description;

    protected List<UrlElement> urlElements;
    protected List<QueryElement> queryElements;
    protected List<String> tags;
    protected List<String> developers;

    protected String contact;
    protected String longName;
    protected String image; // URL defines location of 64x64-pixel image
    protected String attribution;
    protected String language;
    protected String inputEncoding;
    protected String outputEncoding;
    protected boolean adultContent;

    protected int syndicationRight = SyndicationRight.OPEN;

    protected final String defaultInputEncoding = "UTF-8";
    protected final String defaultOutputEncoding = "UTF-8";

    private Document descriptionDocument;

    private Map<String, String> namespaceMap = new HashMap<String, String>();
    private Map<String, String> reverseNamespaceMap = new HashMap<String, String>();

    private List<String> falses = Arrays.asList("false", "FALSE", "0", "no",
            "NO");
    private List<String> synRights = Arrays.asList("open", "limited",
            "private", "closed");

    public OpenSearchDescriptionDocument(Document descriptionDocument) {
        this.descriptionDocument = descriptionDocument;

        try {
            if (!descriptionDocument.getDocumentElement().getNodeName()
                    .equals(ROOT_NODE))
                throw new ElementNodeNotFoundException(ROOT_NODE);

            extractNamespaceMappings();

            shortName = parseSingleElementValue(TAG_SHORTNAME);
            description = parseSingleElementValue(TAG_DESCRIPTION);
            contact = parseSingleElementValue(TAG_CONTACT);
            longName = parseSingleElementValue(TAG_LONGNAME);
            // TODO DEVELOPER CAN BE MULTIPLE !?
            developers = parseMultipleElementValue(TAG_DEVELOPER);
            image = parseSingleElementValue(TAG_IMAGE);
            language = parseSingleElementValue(TAG_LANGUAGE);
            attribution = parseOptSingleElementValue(TAG_ATTRIBUTION);

            String adultContentString = parseSingleElementValue(TAG_ADULTCONTENT);
            if (!falses.contains(adultContentString))
                adultContent = true;

            String syndicationString = parseSingleElementValue(TAG_SYNDICATIONRIGHT);
            syndicationRight = synRights.indexOf(syndicationString);

            String tagsString = parseOptSingleElementValue(TAG_TAGS);
            if (tagsString != null)
                tags = Arrays.asList(tagsString.split(" "));

            inputEncoding = parseSingleElementValue(TAG_INPUTENCODING);
            outputEncoding = parseSingleElementValue(TAG_OUTPUTENCODING);

            extractUrlElements();
            extractQueryElements();
        } catch (ElementNodeNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseOptSingleElementValue(final String elementTag) {
        NodeList nodeList = descriptionDocument
                .getElementsByTagName(elementTag);
        return nodeList.getLength() <= 0 ? null : nodeList.item(0)
                .getFirstChild().getNodeValue().trim();
    }

    private String parseSingleElementValue(final String elementTag)
            throws ElementNodeNotFoundException {
        NodeList nodeList = descriptionDocument
                .getElementsByTagName(elementTag);
        if (nodeList.getLength() <= 0) {
            throw new ElementNodeNotFoundException(elementTag);
        } else {
            return nodeList.item(0).getFirstChild().getNodeValue().trim();
        }
    }

    private List<String> parseMultipleElementValue(final String elementTag)
            throws ElementNodeNotFoundException {
        List<String> resultList = new ArrayList<String>();
        NodeList nodeList = descriptionDocument
                .getElementsByTagName(elementTag);
        if (nodeList.getLength() <= 0)
            throw new ElementNodeNotFoundException(elementTag);
        for (int i = 0; i < nodeList.getLength(); i++) {
            resultList.add(nodeList.item(i).getNodeValue());
        }
        return resultList;
    }

    private void extractUrlElements() {
        urlElements = new ArrayList<UrlElement>();

        NodeList nodeList = descriptionDocument.getElementsByTagName(TAG_URL);
        for (int i = 0; i < nodeList.getLength(); i++) {
            UrlElement urlElement = new UrlElement(reverseNamespaceMap,
                    (Element) nodeList.item(i));
            urlElements.add(urlElement);
        }
        // TODO maybe filter unsupported urls
    }

    private void extractQueryElements() {
        queryElements = new ArrayList<QueryElement>();

        NodeList nodeList = descriptionDocument.getElementsByTagName(TAG_QUERY);
        for (int i = 0; i < nodeList.getLength(); i++) {
            QueryElement queryElement = new QueryElement(
                    (Element) nodeList.item(i));
            queryElements.add(queryElement);
        }
        // TODO maybe filter unsupported urls
    }

    private void extractNamespaceMappings() {
        NamedNodeMap nodeMap = descriptionDocument.getDocumentElement()
                .getAttributes();
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            String namespaceUrl = node.getNodeValue();
            String prefix = node.getLocalName();
            if (!prefix.equals("xmlns")) {
                namespaceMap.put(prefix, namespaceUrl);
                reverseNamespaceMap.put(namespaceUrl, prefix);
            } else {
                namespaceMap.put("", OpenSearchConstants.OPENSEARCH_NAMESPACE);
            }
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public String getContact() {
        return contact;
    }

    public String getLongName() {
        return longName;
    }

    public String getImage() {
        return image;
    }

    public String getAttribution() {
        return attribution;
    }

    public boolean isAdultContent() {
        return adultContent;
    }

    public String getOutputEncoding() {
        return outputEncoding;
    }

    public int getSyndicationRight() {
        return syndicationRight;
    }

}
