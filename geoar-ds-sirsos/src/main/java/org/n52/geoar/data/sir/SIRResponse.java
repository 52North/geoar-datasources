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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.n52.geoar.data.Namespaces;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class SIRResponse {

	private SIRResponse() {
	}

	public static List<SIRResult> getResultsFromResponse(InputStream content)
			throws IOException, SAXException {

		class SIRResultFactory {
			String sirSensorId;
			String description;
			String serviceUrl;
			String serviceType;
			String serviceSensorId;
			Float left, top, bottom, right;

			public SIRResult createSIRMeasurement() {
				SIRResult sirResult = new SIRResult(bottom
						+ ((top - bottom) / 2f), left + ((right - left) / 2f));
				sirResult.description = description;
				sirResult.serviceSensorId = serviceSensorId;
				sirResult.serviceType = serviceType;
				sirResult.serviceUrl = serviceUrl;
				sirResult.sirSensorId = sirSensorId;
				return sirResult;
			}

			public void reset() {
				description = null;
				left = right = top = bottom = null;
				serviceSensorId = null;
				serviceType = null;
				serviceUrl = null;
				sirSensorId = null;
			}

			public void setLowerCorner(String body) {
				String[] split = body.split(" ");
				left = Float.parseFloat(split[1]);
				bottom = Float.parseFloat(split[0]);
			}

			public void setUpperCorner(String body) {
				String[] split = body.split(" ");
				right = Float.parseFloat(split[1]);
				top = Float.parseFloat(split[0]);
			}
		}

		RootElement root = new RootElement(Namespaces.NAMESPACE_SIR,
				"SearchSensorResponse");
		final List<SIRResult> results = new ArrayList<SIRResult>();
		final SIRResultFactory sirResultFactory = new SIRResultFactory();

		Element searchResultElement = root.getChild(Namespaces.NAMESPACE_SIR,
				"SearchResultElement");

		// Parse SearchResultElement node
		searchResultElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes arg0) {
				sirResultFactory.reset();
			}
		});
		searchResultElement.setEndElementListener(new EndElementListener() {
			public void end() {
				results.add(sirResultFactory.createSIRMeasurement());
			}
		});
		searchResultElement.getChild(Namespaces.NAMESPACE_SIR, "SensorIDInSIR")
				.setEndTextElementListener(new EndTextElementListener() {
					public void end(String body) {
						sirResultFactory.sirSensorId = body;
					}
				});

		Element serviceRefElement = searchResultElement.getChild(
				Namespaces.NAMESPACE_SIR, "ServiceReference");
		serviceRefElement.getChild(Namespaces.NAMESPACE_SIR, "ServiceURL")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						sirResultFactory.serviceUrl = body;
					}
				});

		serviceRefElement.getChild(Namespaces.NAMESPACE_SIR, "ServiceType")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						sirResultFactory.serviceType = body;
					}
				});

		serviceRefElement.getChild(Namespaces.NAMESPACE_SIR,
				"ServiceSpecificSensorID").setEndTextElementListener(
				new EndTextElementListener() {
					@Override
					public void end(String body) {
						sirResultFactory.serviceSensorId = body;
					}
				});

		Element simpleDescriptionElement = searchResultElement.getChild(
				Namespaces.NAMESPACE_SIR, "SimpleSensorDescription");
		simpleDescriptionElement.getChild(Namespaces.NAMESPACE_SIR,
				"DescriptionText").setEndTextElementListener(
				new EndTextElementListener() {

					@Override
					public void end(String body) {
						sirResultFactory.description = body;
					}
				});

		Element bboxElement = simpleDescriptionElement.getChild(
				Namespaces.NAMESPACE_SIR, "ObservedBoundingBox");
		bboxElement.getChild(Namespaces.NAMESPACE_OWS, "LowerCorner")
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						sirResultFactory.setLowerCorner(body);
					}
				});
		bboxElement.getChild(Namespaces.NAMESPACE_OWS, "UpperCorner")
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						sirResultFactory.setUpperCorner(body);
					}
				});

		Xml.parse(content, Xml.Encoding.UTF_8, root.getContentHandler());

		return results;
	}

}
