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

import org.n52.geoar.data.opensearch.data.exception.BadQueryParamDefinitionException;
import org.n52.geoar.data.opensearch.data.exception.IncompleteQueryParametersException;
import org.n52.geoar.data.opensearch.data.exception.ParameterNotExistException;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public abstract class QueryBuilderExtension implements OpenSearchQueryBuilder {
	
	protected OpenSearchQueryBuilder queryBuilder;
	
	public QueryBuilderExtension(OpenSearchQueryBuilder queryBuilder){
		this.queryBuilder = queryBuilder;
	}

	@Override
	public boolean hasParameter(String name) {
		return queryBuilder.hasParameter(name);
	}

	@Override
	public boolean isQueryComplete() {
		return queryBuilder.isQueryComplete();
	}

	@Override
	public String getQuery() throws IncompleteQueryParametersException,
			ParameterNotExistException, BadQueryParamDefinitionException {	
		return queryBuilder.getQuery();
	}

	@Override
	public String getParameterValue(String parameterName)
			throws ParameterNotExistException {	
		return queryBuilder.getParameterValue(parameterName);
	}

	@Override
	public boolean areParametersSetted(String... names)
			throws ParameterNotExistException {
		return queryBuilder.areParametersSetted(names);
	}
	
	@Override
	public boolean isParameterSetted(String name)
			throws ParameterNotExistException {
		return queryBuilder.isParameterSetted(name);
	}
	
	@Override
	public OpenSearchQueryBuilder setParameter(String key, String value)
			throws ParameterNotExistException {
		queryBuilder.setParameter(key, value);
		return this;
	}
}
