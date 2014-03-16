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

import org.n52.geoar.data.cosm.CosmFeed;
import org.n52.geoar.newdata.SpatialEntity;

/**
 * 
 * @author Arne de Wall
 * 
 */
public class CosmFeed extends SpatialEntity {

	private static final long serialVersionUID = 1L;

	protected static class Builder {
		private final String id;
		private final String title;
		private final double lat;
		private final double lon;
		
		private double altitude;
		private String locationDomain;
		private String disposition;
		
		private String creator;
		private String status;
		private String version;
		private String feedUrl;
		
		private String lastUpdated;
		private String created;
		
		private String description;

		public Builder(final String id, final String title, final double lat,
				final double lon) {
			this.id = id;
			this.title = title;
			this.lat = lat;
			this.lon = lon;
		}
		
		public Builder setCreator(String creator){
			this.creator = creator;
			return this;
		}
		
		public Builder setStatus(String status){
			this.status = status;
			return this;
		}
		
		public Builder setVersion(String version){
			this.version = version;
			return this;
		}
		
		public Builder setFeedUrl(String feedUrl){
			this.feedUrl = feedUrl;
			return this;
		}
		
		public Builder setLocationDomain(String domain){
			this.locationDomain = domain;
			return this;
		}
		
		public Builder setDisposition(String disposition){
			this.disposition = disposition;
			return this;
		}
		
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public Builder setAltitude(double altitude) {
			this.altitude = altitude;
			return this;
		}

		public Builder setLastUpdated(String lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		public Builder setCreated(String created) {
			this.created = created;
			return this;
		}

		public CosmFeed build(){
			return new CosmFeed(this);
		}

	}

	private final String id;
	private final String title;
	
	private final String locationDomain;
	private final String disposition;
	
	private final String feedUrl;
	private final String status;
	private final String lastUpdated;
	private final String created;
	private final String creator;
	
	private final String version;
	private final String description;



	public CosmFeed(Builder builder) {
		super(builder.lat, builder.lon);
		this.title = builder.title;
		this.id = builder.id;
		this.locationDomain = builder.locationDomain;
		this.disposition = builder.disposition;
		this.feedUrl = builder.feedUrl;
		this.status = builder.status;
		this.lastUpdated = builder.lastUpdated;
		this.created = builder.created;
		this.creator = builder.creator;
		this.version = builder.version;
		this.description = builder.description;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

}
