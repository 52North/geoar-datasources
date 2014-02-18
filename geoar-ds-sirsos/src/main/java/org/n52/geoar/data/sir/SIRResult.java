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
package org.n52.geoar.data.sir;

import org.n52.geoar.newdata.SpatialEntity2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class SIRResult extends SpatialEntity2<Point> {

	private static final long serialVersionUID = 1L;
	private static final GeometryFactory fac = new GeometryFactory();

	String sirSensorId;

	String description;

	String serviceUrl;

	String serviceType;

	String serviceSensorId;

	public SIRResult(double latitude, double longitude) {
		super(fac.createPoint(new Coordinate(latitude, longitude)));
	}

	public String getServiceSensorId() {
		return serviceSensorId;
	}

	public String getSirSensorId() {
		return sirSensorId;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public String getServiceType() {
		return serviceType;
	}

	public String getDescription() {
		return description;
	}

}
