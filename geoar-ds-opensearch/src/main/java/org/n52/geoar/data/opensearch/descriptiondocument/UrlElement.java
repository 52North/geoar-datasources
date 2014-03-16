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
package org.n52.geoar.data.opensearch.descriptiondocument;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.n52.geoar.data.opensearch.exception.ElementAttributeNotFoundException;
import org.w3c.dom.Element;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class UrlElement extends BaseElement {
	private static final String ATTRIB_TEMPLATE = "template";
	private static final String ATTRIB_TYPE = "type";
	private static final String ATTRIB_REL = "rel";
	private static final String ATTRIB_PAGEOFFSET = "pageOffset";
	private static final String ATTRIB_INDEXOFFSET = "indexOffset";

	private String mimeType;

	private String rel;
	private int pageOffset;
	private int indexOffset;
	private UrlTemplate urlTemplate;

	private final List<String> relValues = Arrays.asList("results",
			"suggestions", "self", "collection");


	public UrlElement(Map<String, String> nameSpacePrefixes, Element url) {
		super(OpenSearchDescriptionDocument.TAG_URL, url);
		
		try {
			this.urlTemplate = new UrlTemplate(nameSpacePrefixes,
					parseAttributeNode(ATTRIB_TEMPLATE));
			this.mimeType = parseAttributeNode(ATTRIB_TYPE);
			this.pageOffset = Integer
					.parseInt(parseAttributeNode(ATTRIB_PAGEOFFSET));
			this.indexOffset = Integer
					.parseInt(parseAttributeNode(ATTRIB_INDEXOFFSET));

			String relAttrib = parseAttributeNode(ATTRIB_REL);
			if (relAttrib == null || !relValues.contains(relAttrib))
				this.rel = relValues.get(0); // default
			else
				this.rel = relAttrib;
		} catch (ElementAttributeNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getRel() {
		return rel;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	public int getIndexOffset() {
		return indexOffset;
	}

	public UrlTemplate getUrlTemplate() {
		return urlTemplate;
	}

}
