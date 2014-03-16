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

import org.n52.geoar.newdata.Annotations.Setting;
import org.n52.geoar.newdata.Annotations.Settings.Group;
import org.n52.geoar.newdata.Annotations.Settings.NotNull;
import org.n52.geoar.newdata.Filter;

public class CosmFilter extends Filter {
	private static final long serialVersionUID = 2L;

	public enum RequestSetting {
		AUTO("Automatic", null), EN("English", "en"), DE("German", "de");
		
		private String title;
		private String iso2;
		
		private RequestSetting(String title, String iso2){
			this.title = title;
			this.iso2 = iso2;
		}
		
		@Override
		public String toString(){
			return title;
		}
		
		public String getISO2(){
			return iso2;
		}
		
		public static RequestSetting fromString(String value){
			for(RequestSetting v : values())
				if(value.equalsIgnoreCase(v.name()))
					return v;
			return AUTO;
		}
	}
	
	@Setting
	@NotNull
	private RequestSetting requestSetting = RequestSetting.AUTO;
	
	public RequestSetting getRequestSetting(){
		return requestSetting;
	}

}
