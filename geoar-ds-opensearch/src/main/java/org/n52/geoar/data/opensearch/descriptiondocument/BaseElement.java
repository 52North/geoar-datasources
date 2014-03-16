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

import org.n52.geoar.data.opensearch.exception.ElementAttributeNotFoundException;
import org.n52.geoar.data.opensearch.exception.ElementNodeNotFoundException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public abstract class BaseElement {

	protected final Element element;
	protected final String elementName;

	public BaseElement(final String elementName, final Element element) {
		this.elementName = elementName;
		this.element = element;
	}

	protected String parseAttribute(String elementTag)
			throws ElementNodeNotFoundException {
		String elementValue = element.getAttribute(elementTag);
		if (elementValue == null || elementValue.equals(""))
			throw new ElementNodeNotFoundException(elementTag);
		return elementValue;
	}

	protected String parseAttributeNode(String attrib)
			throws ElementAttributeNotFoundException {
		Node node = element.getAttributeNode(attrib);
		if (node == null)
			throw new ElementAttributeNotFoundException(elementName, attrib);
		return node.getNodeValue();
	}

	protected String parseAttributeNodeNS(String ns, String attrib) throws ElementAttributeNotFoundException {
		Node node = element.getAttributeNodeNS(null, attrib);
		if (node == null) {
			node = element.getAttributeNodeNS(ns, attrib);
			if (node == null)
				throw new ElementAttributeNotFoundException(elementName, attrib);
		}
		return node.getNodeValue().trim();
	}

	protected String parseOptAttributeNodeNS(String ns, String attrib) throws ElementAttributeNotFoundException {
		Node node = element.getAttributeNodeNS(null, attrib);
		if (node == null) {
			node = element.getAttributeNodeNS(ns, attrib);
		}
		return node != null ? node.getNodeValue().trim() : null;
	}

	protected String getElementContent() throws ElementNodeNotFoundException {
		String content = element.getTextContent().trim();
		if (content == null || content.equals(""))
			throw new ElementNodeNotFoundException(elementName);
		return content;
	}

}
