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
package org.n52.geoar.data.opensearch;

import java.util.Calendar;
import java.util.Date;

import org.n52.geoar.data.opensearch.OpenSearchMeasurement.LocationProvider;
import org.n52.geoar.newdata.Annotations.Setting;
import org.n52.geoar.newdata.Annotations.Settings.Group;
import org.n52.geoar.newdata.Annotations.Settings.Max;
import org.n52.geoar.newdata.Annotations.Settings.Min;
import org.n52.geoar.newdata.Annotations.Settings.Name;
import org.n52.geoar.newdata.Annotations.Settings.NoValue;
import org.n52.geoar.newdata.Annotations.Settings.NotNull;
import org.n52.geoar.newdata.Annotations.Settings.Temporal;
import org.n52.geoar.newdata.Annotations.Settings.Temporal.TemporalType;
import org.n52.geoar.newdata.Filter;

public class OpenSearchFilter extends Filter {
	private static final long serialVersionUID = 2L;

	@Setting
	@Group("Noise")
	@Name("Min dB")
	@Min("0")
	@Max("100")
	@NotNull
	private float minDB;

	@Setting
	@Group("Noise")
	@Name("Max dB")
	@Min("0")
	@Max("100")
	@NotNull
	private float maxDB = 120;

	@Setting
	@Group("Location")
	@Name("Location Provider")
	@NoValue("All")
	private LocationProvider locationProvider;

	@Setting
	@Name("Test")
	private String testString;

	@Setting
	@Name("Test Calendar")
	private Calendar testCalendar;

	@Setting
	@Name("Test Date")
	private Date testDate;

	@Setting
	@Name("Test Calendar Time")
	@Temporal(TemporalType.TIME)
	private Calendar testCalTime;

	@Setting
	@Name("Test Calendar Date")
	@Temporal(TemporalType.DATE)
	private Calendar testCalDate;

	public boolean filter(OpenSearchMeasurement measurement) {
		getBoundingBox().sort();
		if (!getBoundingBox().contains(measurement.getLongitude(),
				measurement.getLatitude()))
			return false;
		if (locationProvider != null
				&& !locationProvider.equals(measurement.getProvider()))
			return false;
		if (measurement.getValue() > maxDB)
			return false;
		if (measurement.getValue() < minDB)
			return false;

		return true;
	}

}
