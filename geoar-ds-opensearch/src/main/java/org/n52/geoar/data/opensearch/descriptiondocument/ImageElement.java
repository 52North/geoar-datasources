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

import java.net.URL;

import org.w3c.dom.Element;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class ImageElement extends BaseElement {
	public static final String TAG_WIDTH = "width";
	public static final String TAG_HEIGHT = "height";
	public static final String TAG_MIMETYPE = "type";
	
	private int width;
	private int height;
	private String mimeType;
	private URL url;
	
	public ImageElement(Element image) throws Exception {
		super(OpenSearchDescriptionDocument.TAG_IMAGE, image);
		this.width = Integer.parseInt(parseAttributeNode(TAG_WIDTH));
		this.height = Integer.parseInt(parseAttributeNode(TAG_HEIGHT));
		this.mimeType = parseAttributeNode(TAG_MIMETYPE);
		this.url = new URL(getElementContent());
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getMimeType() {
		return mimeType;
	}

	public URL getUrl() {
		return url;
	}
}
