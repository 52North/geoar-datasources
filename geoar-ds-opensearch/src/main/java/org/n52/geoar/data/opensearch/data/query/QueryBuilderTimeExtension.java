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

/**
 * Specification can be found at: 
 * http://www.opensearch.org/Specifications/OpenSearch/Extensions/Time/1.0/Draft_1
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class QueryBuilderTimeExtension extends QueryBuilderExtension {
	public static final String TIME_QUERY_START = "start";
	public static final String TIME_QUERY_END = "end";
	

	public QueryBuilderTimeExtension(OpenSearchQueryBuilder queryBuilder) {
		super(queryBuilder);
	}

}
