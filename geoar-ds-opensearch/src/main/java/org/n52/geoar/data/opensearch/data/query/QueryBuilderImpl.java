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

import org.n52.geoar.data.opensearch.data.exception.ParameterNotExistException;

public class QueryBuilderImpl implements OpenSearchQueryBuilder {
	
	public QueryBuilderImpl(){
		
	}

	@Override
	public boolean hasParameter(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isQueryComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public OpenSearchQueryBuilder setParameter(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isParameterSetted(String name)
			throws ParameterNotExistException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean areParametersSetted(String... names)
			throws ParameterNotExistException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getParameterValue(String name)
			throws ParameterNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

}
