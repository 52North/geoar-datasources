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

import org.n52.geoar.data.opensearch.OpenSearchConstants;
import org.n52.geoar.data.opensearch.exception.ElementAttributeNotFoundException;
import org.w3c.dom.Element;

/**
 * Specification can be found at:
 * http://www.opensearch.org/Specifications/OpenSearch
 * /1.1#OpenSearch_Query_element
 * 
 * @author Arne de Wall
 * 
 */
public class QueryElement extends BaseElement {
	private static final String QUERY_ATTRIB_ROLE = "role";
	private static final String QUERY_ATTRIB_TITLE = "title";
	private static final String QUERY_ATTRIB_TOTALRESULTS = "totalResults";
	private static final String QUERY_ATTRIB_SEARCHTERMS = "searchTerms";
	private static final String QUERY_ATTRIB_COUNT = "count";
	private static final String QUERY_ATTRIB_STARTINDEX = "startIndex";
	private static final String QUERY_ATTRIB_STARTPAGE = "startPage";
	private static final String QUERY_ATTRIB_LANGUAGE = "language";
	private static final String QUERY_ATTRIB_INPUTENCODING = "inputEncoding";
	private static final String QUERY_ATTRIB_OUTPUTENCODING = "outputEncoding";

	protected String role;
	protected String title;
	protected String totalResults;

	protected String searchTerms;
	protected String count;
	protected String startIndex;
	protected String startPage;
	protected String language;
	protected String inputEncoding;
	protected String outputEncoding;

	public QueryElement(Element query) {
		super(OpenSearchDescriptionDocument.TAG_QUERY, query);
		//@formatter:off
		String ns = OpenSearchConstants.OPENSEARCH_NAMESPACE;
		
		try {
			role			= parseAttributeNodeNS(ns, QUERY_ATTRIB_ROLE);
			title 			= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_TITLE);
			totalResults 	= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_TOTALRESULTS);
			searchTerms 	= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_SEARCHTERMS);
			count 			= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_COUNT);
			startIndex 		= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_STARTINDEX);
			startPage		= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_STARTPAGE);
			language 		= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_LANGUAGE);
			inputEncoding 	= parseOptAttributeNodeNS(ns, QUERY_ATTRIB_INPUTENCODING);
			outputEncoding  = parseOptAttributeNodeNS(ns, QUERY_ATTRIB_OUTPUTENCODING);
		} catch (ElementAttributeNotFoundException e) {
			e.printStackTrace();
		}
		//@formatter:on
	}


	public String getRole() {
		return role;
	}

	public String getTitle() {
		return title;
	}

	public String getTotalResults() {
		return totalResults;
	}

	public String getSearchTerms() {
		return searchTerms;
	}

	public String getCount() {
		return count;
	}

	public String getStartIndex() {
		return startIndex;
	}

	public String getStartPage() {
		return startPage;
	}

	public String getLanguage() {
		return language;
	}

	public String getInputEncoding() {
		return inputEncoding;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}
}
