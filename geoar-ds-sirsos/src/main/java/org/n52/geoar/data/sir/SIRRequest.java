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

import java.io.ByteArrayOutputStream;

import org.n52.geoar.data.Namespaces;
import org.n52.geoar.utils.GeoLocationRect;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class SIRRequest {

	private SIRRequest() {

	}

	public static ByteArrayOutputStream createSearchSensorRequestXML(
			GeoLocationRect bbox) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			XmlSerializer s = Xml.newSerializer();
			s.setOutput(stream, "UTF-8");
			s.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					Boolean.TRUE);

			s.startDocument("UTF-8", false);

			s.setPrefix("", Namespaces.NAMESPACE_SIR);
			s.setPrefix(Namespaces.NAMESPACE_SIR_PREFIX,
					Namespaces.NAMESPACE_SIR);
			
			// Somehow SIR wants the namespace of some attributes to be set
			// explicitly
			s.setPrefix(Namespaces.NAMESPACE_OWS_PREFIX,
					Namespaces.NAMESPACE_OWS);
			s.startTag(Namespaces.NAMESPACE_SIR, "SearchSensorRequest");
			s.attribute("", "service", "SIR");
			s.attribute(Namespaces.NAMESPACE_SIR, "version", "0.3.1");

			s.startTag(Namespaces.NAMESPACE_SIR, "SearchCriteria");
			s.startTag(Namespaces.NAMESPACE_SIR, "BoundingBox");

			bbox.sort();
			// top / bottom vertauschen da durch sort() top <= bottom
			// gilt, geografische Koordinaten.

			s.attribute(Namespaces.NAMESPACE_SIR, "crs",
					"urn:ogc:def:crs:EPSG:6.14:4326");
			s.attribute(Namespaces.NAMESPACE_SIR, "dimensions", "2");
			s.startTag(Namespaces.NAMESPACE_OWS, "LowerCorner");
			s.text(bbox.top + " " + bbox.left);
			s.endTag(Namespaces.NAMESPACE_OWS, "LowerCorner");
			s.startTag(Namespaces.NAMESPACE_OWS, "UpperCorner");
			s.text(bbox.bottom + " " + bbox.right);
			s.endTag(Namespaces.NAMESPACE_OWS, "UpperCorner");

			s.endTag(Namespaces.NAMESPACE_SIR, "BoundingBox");
			s.endTag(Namespaces.NAMESPACE_SIR, "SearchCriteria");

			s.startTag(Namespaces.NAMESPACE_SIR, "SimpleResponse");
			s.text("true");
			s.endTag(Namespaces.NAMESPACE_SIR, "SimpleResponse");

			s.endTag(Namespaces.NAMESPACE_SIR, "SearchSensorRequest");
			s.endDocument();
			s.flush();
			stream.close();
			return stream;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
