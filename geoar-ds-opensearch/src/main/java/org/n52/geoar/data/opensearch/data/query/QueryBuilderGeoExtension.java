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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.geoar.data.opensearch.data.exception.BadQueryParamDefinitionException;
import org.n52.geoar.data.opensearch.data.exception.IncompleteQueryParametersException;
import org.n52.geoar.data.opensearch.data.exception.ParameterNotExistException;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
public class QueryBuilderGeoExtension extends QueryBuilderExtension {

	public static final String GEO_QUERY_NAME = "name";
	public static final String GEO_QUERY_LAT = "lat";
	public static final String GEO_QUERY_LON = "lon";
	public static final String GEO_QUERY_RAD = "radius";
	public static final String GEO_QUERY_BOX = "box";
	public static final String GEO_QUERY_GEOM = "geometry";

	private static final List<String> QUERY_ATTRIB_LIST = Arrays.asList(
			GEO_QUERY_BOX, GEO_QUERY_GEOM, GEO_QUERY_LAT, GEO_QUERY_LON,
			GEO_QUERY_RAD);

	private static final Map<String, Validator> validatorMap = new HashMap<String, Validator>();

	private static interface Validator {
		void validate(String parameter, String value)
				throws BadQueryParamDefinitionException;
	}

	private static Validator bboxValidator = new Validator() {

		@Override
		public void validate(String parameter, String value)
				throws BadQueryParamDefinitionException {
			String[] coords = value.split(",");
			if (coords.length != 4)
				throw new BadQueryParamDefinitionException(
						"[geo:box] incorrect number of coordinates (="
								+ coords.length + ")");

			double west = Double.parseDouble(coords[0]);
			double south = Double.parseDouble(coords[1]);
			double east = Double.parseDouble(coords[2]);
			double north = Double.parseDouble(coords[3]);
			//@formatter:off
			if((west < -180.0d || west > 180.0d))
				throw new BadQueryParamDefinitionException("[geo:box] incorrect west value");
			if((east < -180.0d || east > 180.0d))
				throw new BadQueryParamDefinitionException("[geo:box] incorrect east value");
			if((north < -90.0d || north > 90.0d))
				throw new BadQueryParamDefinitionException("[geo:box] incorrect north value");
			if((south < -90.0d || south > 90.0d))
				throw new BadQueryParamDefinitionException("[geo:box] incorrect south value");
			//@formatter:on
		}

	};

	private static Validator geometryValidator = new Validator() {

		@Override
		public void validate(String parameter, String value)
				throws BadQueryParamDefinitionException {
			// TODO Auto-generated method stub

		}
	};

	private static final Validator latValidator = new Validator() {
		@Override
		public void validate(String parameter, String value)
				throws BadQueryParamDefinitionException {
			double v = Double.parseDouble(value);
			if (v < -90.0d || v > 90.0d)
				throw new BadQueryParamDefinitionException(
						"[geo:lat] incorrect lat value");
		}
	};

	private static final Validator lonValidator = new Validator() {
		@Override
		public void validate(String parameter, String value)
				throws BadQueryParamDefinitionException {
			double v = Double.parseDouble(value);
			if (v < -180.0d || v > 180.0d)
				throw new BadQueryParamDefinitionException(
						"[geo:lon] incorrect lon value");
		}
	};

	private static final Validator radiusValidator = new Validator() {
		@Override
		public void validate(String parameter, String value)
				throws BadQueryParamDefinitionException {
			double v = Double.parseDouble(value);
			if (v <= 0.0d)
				throw new BadQueryParamDefinitionException(
						"[geo:radius] incorrect radius value (=" + value + ")");
		}
	};

	static {
		validatorMap.put(GEO_QUERY_BOX, bboxValidator);
		validatorMap.put(GEO_QUERY_GEOM, geometryValidator);
		validatorMap.put(GEO_QUERY_LAT, latValidator);
		validatorMap.put(GEO_QUERY_LON, lonValidator);
		validatorMap.put(GEO_QUERY_RAD, radiusValidator);
	}

	public QueryBuilderGeoExtension(OpenSearchQueryBuilder queryBuilder) {
		super(queryBuilder);
	}

	@Override
	public boolean hasParameter(String name) {
		return super.hasParameter(name);
	}

	@Override
	public boolean isQueryComplete() {
		return super.isQueryComplete();
	}

	@Override
	public String getQuery() throws IncompleteQueryParametersException,
			ParameterNotExistException, BadQueryParamDefinitionException {
		
		int spatialFilterCount = 0;
		if (isParameterSetted(GEO_QUERY_NAME))
			spatialFilterCount++;
		if (isParameterSetted(GEO_QUERY_BOX))
			spatialFilterCount++;
		if (isParameterSetted(GEO_QUERY_GEOM))
			spatialFilterCount++;
		if (isParameterSetted(GEO_QUERY_LAT)
				|| isParameterSetted(GEO_QUERY_LON)
				|| isParameterSetted(GEO_QUERY_RAD)) {
			spatialFilterCount++;

			if (!isParameterSetted(GEO_QUERY_LAT)
					&& !isParameterSetted(GEO_QUERY_LON)
					&& !isParameterSetted(GEO_QUERY_RAD)) {
				throw new BadQueryParamDefinitionException(
						"[geo:...] One/Two of lat, lon or rad attributes is/are not setted");
			}
		}
		if (spatialFilterCount > 1)
			throw new BadQueryParamDefinitionException(
					"[geo:...] Multiple filters setted");

		for (String key : validatorMap.keySet()) {
			String value = getParameterValue(key);
			validatorMap.get(key).validate(key, value);
		}
		
		return queryBuilder.getQuery();
	}

}
