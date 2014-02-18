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
package org.n52.geoar.data.wiki;

import org.n52.geoar.newdata.SpatialEntity2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * @author Arne de Wall
 * 
 */
public class WikiResult extends SpatialEntity2<Point> {

    private static GeometryFactory fac = new GeometryFactory();
	private static final long serialVersionUID = 1L;

	private final String mId;
	private final String mTitle;
	private String mUrl;

	public WikiResult(String id, String title, double latitude, double longitude){
		super(fac.createPoint(new Coordinate(longitude, latitude)));
		this.mTitle = title;
		this.mId = id;
	}
	
	public WikiResult(String id, String title, double latitude, double longitude, double altitude){
		super(fac.createPoint(new Coordinate(longitude, latitude, altitude)));
		this.mTitle = title;
		this.mId = id;
	}

	public String getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}
}
