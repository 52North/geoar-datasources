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
package org.n52.geoar.data.cosm;

import org.n52.geoar.utils.GeoLocationRect;

import android.location.Location;

public class CosmRequest {
	
	private static final String API_KEY = "${conf.cosm.key}";
	private static final String url = "http://api.cosm.com/v2/feeds?key=" + API_KEY;

	private static final String LAT = "lat";
	private static final String LON = "lon";
	private static final String RADIUS = "distance";
	private static final String DISTANCE_UNITS = "distance_units";
	
	private CosmRequest(){ }
	
	static String buildRequest(CosmFilter filter){ 
		GeoLocationRect rect = filter.getBoundingBox();
		StringBuilder builder = new StringBuilder();
		rect.sort();
		
		float[] distance = new float[1];
		Location.distanceBetween(rect.getLatitude(), rect.getLongitude(), rect.bottom, rect.left, distance);
		
		builder.append(url);
		builder.append("&" + LAT + "=" + rect.getLatitude() + "&");
		builder.append(LON + "=" + rect.getLongitude() + "&");
		builder.append(RADIUS + "=" + distance[0]/1000.0f + "&");
		builder.append(DISTANCE_UNITS + "=kms");
		
//		switch(filter.getRequestSetting()){
//		case AUTO:
//			builder.append(LOCALE + "=" + Locale.getDefault().getLanguage());
//			break;
//		default: 
//			builder.append(LOCALE + "=" + filter.getRequestSetting().getISO2());
//			break;
//		}
		return builder.toString();
	}
	
}
