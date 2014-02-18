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
package org.n52.geoar.data.noise;

import java.util.Calendar;

import org.n52.geoar.newdata.SpatialObservation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class NoiseMeasurement extends SpatialObservation<Point, Float> {

    private static final GeometryFactory fac = new GeometryFactory();
    
	public enum LocationProvider {
		USER("User"), GPS("GPS"), NETWORK("W-LAN"), OTHER("Other");

		private String title;

		private LocationProvider(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}

		public static LocationProvider fromString(String value) {
			for (LocationProvider v : values())
				if (value.equalsIgnoreCase(v.name()))
					return v;
			return OTHER;
		}
	};

	private static final long serialVersionUID = 1L;
	private float accuracy;

	public NoiseMeasurement(double latitude, double longitude, Float value) {
		super(fac.createPoint(new Coordinate(longitude, latitude)), value);
	}

	private LocationProvider provider;
	private Calendar time;
	private Float moral;

	public LocationProvider getProvider() {
		return provider;
	}

	public void setProvider(LocationProvider provider) {
		this.provider = provider;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public Calendar getTime() {
		return time;
	}

	public Float getMoral() {
		return moral;
	}
	
	public void setMoral(Float moral) {
		this.moral = moral;
	}
	
	public void setAccuracy(float accuracy){
	    this.accuracy = accuracy;
	}
	
	public float getAccuracy(){
	    return this.accuracy;
	}

}
