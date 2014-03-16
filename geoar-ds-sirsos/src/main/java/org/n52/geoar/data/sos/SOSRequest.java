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
package org.n52.geoar.data.sos;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.n52.geoar.data.DateUtils;
import org.n52.geoar.data.Namespaces;
import org.n52.geoar.data.sos.SOSResponse.Offering;
import org.n52.geoar.utils.GeoLocationRect;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class SOSRequest {

	public static ByteArrayOutputStream createCapabilitiesRequestXML() {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			XmlSerializer s = Xml.newSerializer();
			s.setOutput(stream, "UTF-8");
			s.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					Boolean.TRUE);

			s.startDocument("UTF-8", false);

			s.setPrefix("", Namespaces.NAMESPACE_SOS);
			s.setPrefix(Namespaces.NAMESPACE_OWS_PREFIX,
					Namespaces.NAMESPACE_OWS);
			s.startTag(Namespaces.NAMESPACE_SOS, "GetCapabilities");
			s.attribute("", "service", "SOS");

			// AcceptVersion
			s.startTag(Namespaces.NAMESPACE_OWS, "AcceptVersions");
			s.startTag(Namespaces.NAMESPACE_OWS, "Version");
			s.text("1.0.0");
			s.endTag(Namespaces.NAMESPACE_OWS, "Version");
			s.endTag(Namespaces.NAMESPACE_OWS, "AcceptVersions");

			// Sections
			String[] requiredSections = new String[] { "Contents",
					"ServiceIdentification" };
			s.startTag(Namespaces.NAMESPACE_OWS, "Sections");
			for (String section : requiredSections) {
				s.startTag(Namespaces.NAMESPACE_OWS, "Section");
				s.text(section);
				s.endTag(Namespaces.NAMESPACE_OWS, "Section");
			}
			s.endTag(Namespaces.NAMESPACE_OWS, "Sections");

			s.endTag(Namespaces.NAMESPACE_SOS, "GetCapabilities");
			s.endDocument();
			s.flush();
			stream.close();
			return stream;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static ByteArrayOutputStream createFeatureOfInterestRequestXML(
			GeoLocationRect bbox, boolean useEpsgVersion) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			XmlSerializer s = Xml.newSerializer();
			s.setOutput(stream, "UTF-8");
			s.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					Boolean.TRUE);

			s.startDocument("UTF-8", false);

			s.setPrefix("", Namespaces.NAMESPACE_SOS);
			s.setPrefix(Namespaces.NAMESPACE_OWS_PREFIX,
					Namespaces.NAMESPACE_OWS);
			s.setPrefix(Namespaces.NAMESPACE_OGC_PREFIX,
					Namespaces.NAMESPACE_OGC);
			s.setPrefix(Namespaces.NAMESPACE_GML_PREFIX,
					Namespaces.NAMESPACE_GML);
			s.startTag(Namespaces.NAMESPACE_SOS, "GetFeatureOfInterest");
			s.attribute("", "service", "SOS");
			s.attribute("", "version", "1.0.0");

			// location
			s.startTag(Namespaces.NAMESPACE_SOS, "location");
			s.startTag(Namespaces.NAMESPACE_OGC, "BBOX");

			s.startTag(Namespaces.NAMESPACE_OGC, "PropertyName");
			s.text("urn:ogc:data:location");
			s.endTag(Namespaces.NAMESPACE_OGC, "PropertyName");

			// Envelope
			s.startTag(Namespaces.NAMESPACE_GML, "Envelope");
			if (useEpsgVersion) {
				s.attribute("", "srsName", "urn:ogc:def:crs:EPSG::4326");
			} else {
				s.attribute("", "srsName", "urn:ogc:def:crs:EPSG:4326");
			}

			bbox.sort();
			// top <= bottom
			s.startTag(Namespaces.NAMESPACE_GML, "lowerCorner");
			s.text(bbox.top + " " + bbox.left);
			s.endTag(Namespaces.NAMESPACE_GML, "lowerCorner");

			s.startTag(Namespaces.NAMESPACE_GML, "upperCorner");
			s.text(bbox.bottom + " " + bbox.right);
			s.endTag(Namespaces.NAMESPACE_GML, "upperCorner");
			s.endTag(Namespaces.NAMESPACE_GML, "Envelope");

			s.endTag(Namespaces.NAMESPACE_OGC, "BBOX");
			s.endTag(Namespaces.NAMESPACE_SOS, "location");

			s.endTag(Namespaces.NAMESPACE_SOS, "GetFeatureOfInterest");
			s.endDocument();
			s.flush();
			stream.close();
			return stream;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static ByteArrayOutputStream createObservationRequestXML(
			Offering offering, String procedure, Date begin, Date end,
			FOI featureOfInterest) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			XmlSerializer s = Xml.newSerializer();
			s.setOutput(stream, "UTF-8");
			s.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					Boolean.TRUE);

			s.startDocument("UTF-8", false);

			s.setPrefix("", Namespaces.NAMESPACE_SOS);
			s.setPrefix(Namespaces.NAMESPACE_OWS_PREFIX,
					Namespaces.NAMESPACE_OWS);
			s.setPrefix(Namespaces.NAMESPACE_OM_PREFIX, Namespaces.NAMESPACE_OM);
			s.setPrefix(Namespaces.NAMESPACE_OGC_PREFIX,
					Namespaces.NAMESPACE_OGC);
			s.setPrefix(Namespaces.NAMESPACE_GML_PREFIX,
					Namespaces.NAMESPACE_GML);
			s.startTag(Namespaces.NAMESPACE_SOS, "GetObservation");
			s.attribute("", "service", "SOS");
			s.attribute("", "version", "1.0.0");
			// s.attribute("", "srsName", "urn:ogc:def:crs:EPSG::4326");

			// Offering
			s.startTag(Namespaces.NAMESPACE_SOS, "offering");
			s.text(offering.getId());
			s.endTag(Namespaces.NAMESPACE_SOS, "offering");

			// event Time
			s.startTag(Namespaces.NAMESPACE_SOS, "eventTime");
			s.startTag(Namespaces.NAMESPACE_OGC, "TM_During");

			s.startTag(Namespaces.NAMESPACE_OGC, "PropertyName");
			s.text("om:samplingTime");
			s.endTag(Namespaces.NAMESPACE_OGC, "PropertyName");

			s.startTag(Namespaces.NAMESPACE_GML, "TimePeriod");

			s.startTag(Namespaces.NAMESPACE_GML, "beginPosition");
			s.text(DateUtils.getISOString(begin));
			s.endTag(Namespaces.NAMESPACE_GML, "beginPosition");

			s.startTag(Namespaces.NAMESPACE_GML, "endPosition");
			s.text(DateUtils.getISOString(end));
			s.endTag(Namespaces.NAMESPACE_GML, "endPosition");

			s.endTag(Namespaces.NAMESPACE_GML, "TimePeriod");

			s.endTag(Namespaces.NAMESPACE_OGC, "TM_During");
			s.endTag(Namespaces.NAMESPACE_SOS, "eventTime");

			// Procedure
			if (procedure != null) {
				s.startTag(Namespaces.NAMESPACE_SOS, "procedure");
				s.text(procedure);
				s.endTag(Namespaces.NAMESPACE_SOS, "procedure");
			}

			// Observed Properties
			for (String observedProperty : offering.getObservedProperties()) {
				s.startTag(Namespaces.NAMESPACE_SOS, "observedProperty");
				s.text(observedProperty);
				s.endTag(Namespaces.NAMESPACE_SOS, "observedProperty");
			}

			if (featureOfInterest != null) {
				s.startTag(Namespaces.NAMESPACE_SOS, "featureOfInterest");
				s.startTag(Namespaces.NAMESPACE_SOS, "ObjectID");
				s.text(featureOfInterest.id);
				s.endTag(Namespaces.NAMESPACE_SOS, "ObjectID");
				s.endTag(Namespaces.NAMESPACE_SOS, "featureOfInterest");
			}

			// Response Format
			s.startTag(Namespaces.NAMESPACE_SOS, "responseFormat");
			s.text("text/xml;subtype=\"om/1.0.0\"");
			s.endTag(Namespaces.NAMESPACE_SOS, "responseFormat");

			// Result Model
			s.startTag(Namespaces.NAMESPACE_SOS, "resultModel");
			s.text("om:Observation"); // Because swe:DataArray allows shorter
										// response
			s.endTag(Namespaces.NAMESPACE_SOS, "resultModel");

			s.endTag(Namespaces.NAMESPACE_SOS, "GetObservation");
			s.endDocument();
			s.flush();
			stream.close();
			return stream;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private SOSRequest() {

	}
}
