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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.n52.geoar.data.MeasureParserException;
import org.n52.geoar.data.noise.NoiseMeasurement.LocationProvider;
import org.n52.geoar.newdata.Annotations;
import org.n52.geoar.newdata.Annotations.Settings.Name;
import org.n52.geoar.newdata.Annotations.SupportedVisualization;
import org.n52.geoar.newdata.DataSource;
import org.n52.geoar.newdata.SpatialEntity2;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Environment;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Data source to access locally saved measurements of the NoiseDroid
 * application. Intended just for testing purposes
 * 
 * @author Holger Hopmann
 * 
 */
@Annotations.DataSource(name = @Name("NoiseDroid"), cacheZoomLevel = 0, minReloadInterval = -1, description = "Example for a description")
@SupportedVisualization(visualizationClasses = { NoiseMapVisualization.class,
		NoiseARVisualization.class })
public class NoiseDroidLocalSource implements DataSource<NoiseFilter> {

	private static String noiseDroidMeasurementsPath = Environment
			.getExternalStorageDirectory()
			+ "/Android/data/de.noisedroid/measures.xml";

	@Override
	public List<? extends SpatialEntity2<? extends Geometry>> getMeasurements(NoiseFilter filter) {
		try {
			Reader reader = new FileReader(noiseDroidMeasurementsPath);

			List<NoiseMeasurement> measurements = getMeasuresFromResponse(reader);

			Iterator<NoiseMeasurement> iterator = measurements.iterator();
			while (iterator.hasNext()) {
				if (!filter.filter(iterator.next())) {
					iterator.remove();
				}
			}

			return measurements;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public boolean isAvailable() {
		// NoiseDroid data is available if specific file exists on sd card
		return new File(noiseDroidMeasurementsPath).canRead();
	}

	public static List<NoiseMeasurement> getMeasuresFromResponse(Reader reader)
			throws RequestException {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(reader);
			return getMeasuresFromParser(parser);
		} catch (Exception e) {
			throw new RequestException(e.getMessage());
		}
	}

	private static List<NoiseMeasurement> getMeasuresFromParser(
			XmlPullParser parser) throws RequestException {
		try {
			List<NoiseMeasurement> measureResultList = new ArrayList<NoiseMeasurement>();
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (parser.getName().equals("Measure")) {
						measureResultList.add(getMeasureFromXML(parser));
					} else if (parser.getName().equals("Error")) {
						return null;
					}
				}
				eventType = parser.next();
			}
			parser.setInput(null);
			return measureResultList;
		} catch (Exception e) {
			throw new RequestException(e.getMessage());
		}
	}

	public static NoiseMeasurement getMeasureFromXML(XmlPullParser parser)
			throws MeasureParserException {
		try {

			Float value = null, accuracy = null, moral = null;
			Double latitude = null, longitude = null;
			LocationProvider provider = null;
			Calendar time = null;

			while (true) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					if (parser.getName().equals("Time")) {
						time = stringToCalendar(parser.getAttributeValue(null,
								"value"));
					} else if (parser.getName().equals("LocationMeasure")) {
						longitude = Double.parseDouble(parser
								.getAttributeValue(null, "longitude"));
						latitude = Double.parseDouble(parser.getAttributeValue(
								null, "latitude"));
						accuracy = Float.parseFloat(parser.getAttributeValue(
								null, "accuracy"));
						provider = LocationProvider.fromString(parser
								.getAttributeValue(null, "provider"));
					} else if (parser.getName().equals("NoiseMeasure")) {
						value = Float.parseFloat(parser.getAttributeValue(null,
								"value"));
					} else if (parser.getName().equals("Moral")) {
						moral = Float.parseFloat(parser.getAttributeValue(null,
								"value"));
					}

				}

				// Abbruch
				if (parser.getEventType() == XmlPullParser.END_TAG
						&& parser.getName().equals("Measure")) {
					break;
				}

				parser.next();
			}
			NoiseMeasurement m = new NoiseMeasurement(latitude, longitude,
					value);
			m.setTime(time);
			m.setProvider(provider);
			m.setAccuracy(accuracy);
			m.setMoral(moral);
			return m;
		} catch (Exception e) {
			throw new MeasureParserException(e.getMessage());
		}
	}

	public static Calendar stringToCalendar(String timeString) {
		Locale locale = Locale.GERMAN;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
				locale);
		try {
			Date d = format.parse(timeString);
			Calendar c = new GregorianCalendar();
			c.setTime(d);

			return c;
		} catch (ParseException e) {
			return null;
		}
	}

}
