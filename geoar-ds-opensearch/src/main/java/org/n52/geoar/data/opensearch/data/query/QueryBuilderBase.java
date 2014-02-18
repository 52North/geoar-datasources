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
package org.n52.geoar.data.opensearch.data.query;

import java.util.HashMap;
import java.util.Map;

import org.n52.geoar.data.opensearch.data.descriptiondocument.UrlTemplate;
import org.n52.geoar.data.opensearch.data.exception.IncompleteQueryParametersException;
import org.n52.geoar.data.opensearch.data.exception.ParameterNotExistException;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class QueryBuilderBase implements OpenSearchQueryBuilder {	
	protected final UrlTemplate template;
	protected Map<String, String> values = new HashMap<String, String>();
	
	public QueryBuilderBase(final UrlTemplate template){
		this.template = template;
		
		for(String parameter : template.getRequiredParameters()){
			values.put(parameter, null);
		}
		for(String parameter : template.getOptionalParameters()){
			values.put(parameter, "");
		}
		
//		values.get(OpenSearchConstants.DEFAULT_ECODING)
	}

	@Override
	public boolean hasParameter(String name) {
		return template.hasParameter(name);
	}

	@Override
	public boolean isQueryComplete() {
		return values.containsValue(null) ? false : true;
	}

	@Override
	public OpenSearchQueryBuilder setParameter(String name, String value) throws ParameterNotExistException {
		if(hasParameter(name))
			throw new ParameterNotExistException(name);
		values.put(name, value);
		return this;
	}

	@Override
	public String getQuery() throws IncompleteQueryParametersException {
		if(isQueryComplete() == false)
			throw new IncompleteQueryParametersException();
		
		String templateString = template.getTemplate();
		for(Map.Entry<String, String> e : values.entrySet()){
			if(template.isRequiredParameter(e.getKey()))
				templateString = templateString.replaceAll("\\{" + e.getKey() + "\\}", e.getValue());
			else
				templateString = templateString.replaceAll("\\{" + e.getKey() + "\\?\\}", e.getValue());
		}
		return templateString;
	}

	@Override
	public boolean isParameterSetted(String name) throws ParameterNotExistException {
		if(!template.hasParameter(name))
			throw new ParameterNotExistException(name);
		return !(values.get(name) == null || values.get(name).equals(""));
	}

	@Override
	public String getParameterValue(String name) throws ParameterNotExistException {
		if(!template.hasParameter(name))
			throw new ParameterNotExistException(name);
		return values.get(name);
	}

	@Override
	public boolean areParametersSetted(String... names)
			throws ParameterNotExistException {
		for(String name : names){
			if(!isParameterSetted(name))
				return false;
		}
		return true;
	}

}
