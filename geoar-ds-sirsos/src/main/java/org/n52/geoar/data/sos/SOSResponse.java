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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.n52.geoar.data.DateUtils;
import org.n52.geoar.data.Namespaces;
import org.n52.geoar.data.sos.SOSResponse.Observation.Value;
import org.n52.geoar.newdata.PluginException;
import org.n52.geoar.utils.GeoLocation;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class SOSResponse {

	public static class OWSException extends PluginException {

		private static final long serialVersionUID = 1L;
		private String code;

		/**
		 * @param detailMessage
		 */
		public OWSException(String code, String text) {
			super(code, text);
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}

	/**
	 * Special {@link RootElement} which essentially allows to ignore the
	 * concept of a single root element, so that the android.sax classes/helpers
	 * can be used with XML structures having varying root element names (e.g.
	 * normal response + error response).
	 */
	private static class NoRootElement extends RootElement {

		public NoRootElement() {
			super(""); // new root with empty string as name
		}

		@Override
		public ContentHandler getContentHandler() {
			return mHandler;
		}

		private ContentHandler mBaseHandler = super.getContentHandler();
		private ContentHandler mHandler = new DefaultHandler() {

			private int depth = 0;

			// Intercept SAX events for startElement/endElement
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				int depth = this.depth++;

				if (depth == 0) {
					// Skip the root by wrapping a new root with empty string as
					// name around the real root
					mBaseHandler.startElement("", "", "", null);
				}

				mBaseHandler.startElement(uri, localName, qName, attributes);
			}

			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				depth--;
				mBaseHandler.endElement(uri, localName, qName);
				if (depth == 0) {
					mBaseHandler.endElement("", "", "");
				}
			}

			@Override
			public void endDocument() throws SAXException {
				mBaseHandler.endDocument();
			}

			@Override
			public void characters(char[] ch, int start, int length)
					throws SAXException {
				mBaseHandler.characters(ch, start, length);
			}

			@Override
			public void endPrefixMapping(String prefix) throws SAXException {
				mBaseHandler.endPrefixMapping(prefix);
			}

			public void ignorableWhitespace(char[] ch, int start, int length)
					throws SAXException {
				mBaseHandler.ignorableWhitespace(ch, start, length);
			};

			@Override
			public void processingInstruction(String target, String data)
					throws SAXException {
				// TODO Auto-generated method stub
				mBaseHandler.processingInstruction(target, data);
			}

			@Override
			public void setDocumentLocator(Locator locator) {
				mBaseHandler.setDocumentLocator(locator);
			}

			@Override
			public void skippedEntity(String name) throws SAXException {
				mBaseHandler.skippedEntity(name);
			}

			@Override
			public void startDocument() throws SAXException {
				mBaseHandler.startDocument();
			}

			@Override
			public void startPrefixMapping(String prefix, String uri)
					throws SAXException {
				mBaseHandler.startPrefixMapping(prefix, uri);
			}

		};

	}

	public static class CapabilitiesResult {
		private List<Offering> offerings;
		private ServiceIdentifiation serviceIdentifiation;

		public CapabilitiesResult(List<Offering> offerings,
				ServiceIdentifiation serviceIdentifiation) {
			this.offerings = offerings;
			this.serviceIdentifiation = serviceIdentifiation;
		}

		public List<Offering> getOfferings() {
			return offerings;
		}

		public ServiceIdentifiation getServiceIdentifiation() {
			return serviceIdentifiation;
		}
	}

	public static class Offering {
		private String[] observedProperties;
		private String name;
		private String id;
		private Date begin, end;

		public Offering(String id, String name, String[] observedProperties,
				Date begin, Date end) {
			this.observedProperties = observedProperties;
			this.id = id;
			this.name = name;
			this.begin = begin;
			this.end = end;
		}

		public String[] getObservedProperties() {
			return observedProperties;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return name;
		}

		public String getName() {
			return name;
		}

		public Date getBegin() {
			return begin;
		}

		public Date getEnd() {
			return end;
		}
	}

	public static class ServiceIdentifiation {
		String title;
		String abstractText;
		String fees;
		String accessConstraints;
		String serviceUrl;

		public String getTitle() {
			return title;
		}
	}

	public static class Observation {
		private String uom;
		private List<Value> values;

		public Observation(List<Value> values, String uom) {
			this.uom = uom;
			this.values = values;
		}

		public static class Value {
			private float quantity;
			private Date samplingTime;

			public Value(Date samplingTime, float quantity) {
				this.samplingTime = samplingTime;
				this.quantity = quantity;
			}

			public Date getSamplingTime() {
				return samplingTime;
			}

			public float getQuantity() {
				return quantity;
			}
		}

		public List<Value> getValues() {
			return values;
		}

		public String getUom() {
			return uom;
		}
	}

	public static CapabilitiesResult processCapabilitiesResponse(
			InputStream content, String serviceUrl) throws IOException,
			SAXException {
		class OfferingFactory {
			String id;
			String name;
			Date begin, end;
			List<String> observedProperties = new ArrayList<String>();

			public void addObservedProperty(String value) {
				observedProperties.add(value);
			}

			public Offering createOffering() {

				return new Offering(id, name,
						observedProperties.toArray(new String[] {}), begin, end);
			}

			public void reset() {
				id = null;
				name = null;
				observedProperties.clear();
				begin = end = null;
			}
		}

		final List<Offering> offerings = new ArrayList<SOSResponse.Offering>();
		final ServiceIdentifiation serviceIdentifiation = new ServiceIdentifiation();
		serviceIdentifiation.serviceUrl = serviceUrl;
		RootElement root = new RootElement(Namespaces.NAMESPACE_SOS,
				"Capabilities");
		final OfferingFactory observationOfferingFactory = new OfferingFactory();

		Element observationOffering = root
				.getChild(Namespaces.NAMESPACE_SOS, "Contents")
				.getChild(Namespaces.NAMESPACE_SOS, "ObservationOfferingList")
				.getChild(Namespaces.NAMESPACE_SOS, "ObservationOffering");

		// Parse ObservationOfferings
		observationOffering.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				observationOfferingFactory.reset();
				observationOfferingFactory.id = attributes.getValue("gml:id");
				// No namespace in result
			}
		});
		observationOffering.setEndElementListener(new EndElementListener() {
			public void end() {
				offerings.add(observationOfferingFactory.createOffering());
			}
		});
		observationOffering.requireChild(Namespaces.NAMESPACE_GML, "name")
				.setEndTextElementListener(new EndTextElementListener() {
					public void end(String body) {
						observationOfferingFactory.name = body;
					}
				});

		observationOffering.getChild(Namespaces.NAMESPACE_SOS,
				"observedProperty").setStartElementListener(
				new StartElementListener() {

					@Override
					public void start(Attributes attributes) {
						observationOfferingFactory
								.addObservedProperty(attributes
										.getValue("xlink:href"));
					}
				});

		Element timePeriodElement = observationOffering.getChild(
				Namespaces.NAMESPACE_SOS, "time").getChild(
				Namespaces.NAMESPACE_GML, "TimePeriod");
		timePeriodElement.getChild(Namespaces.NAMESPACE_GML, "beginPosition")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						observationOfferingFactory.begin = DateUtils
								.getDateFromISOString(body);
					}
				});
		timePeriodElement.getChild(Namespaces.NAMESPACE_GML, "endPosition")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						observationOfferingFactory.end = DateUtils
								.getDateFromISOString(body);
					}
				});

		// Service Identification
		Element serviceIdentification = root.getChild(Namespaces.NAMESPACE_OWS,
				"ServiceIdentification");
		serviceIdentification.getChild(Namespaces.NAMESPACE_OWS, "Title")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						serviceIdentifiation.title = body;
					}
				});
		serviceIdentification.getChild(Namespaces.NAMESPACE_OWS, "Abstract")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						serviceIdentifiation.abstractText = body;
					}
				});

		serviceIdentification.getChild(Namespaces.NAMESPACE_OWS, "Fees")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						serviceIdentifiation.fees = body;
					}
				});
		serviceIdentification.getChild(Namespaces.NAMESPACE_OWS,
				"AccessConstraints").setEndTextElementListener(
				new EndTextElementListener() {
					@Override
					public void end(String body) {
						serviceIdentifiation.accessConstraints = body;
					}
				});
		ContentHandler contentHandler = root.getContentHandler();
		// Pretend a prefix mapping event with the GML namespace, as it
		// is not included in most responses
		contentHandler.startPrefixMapping(Namespaces.NAMESPACE_GML_PREFIX,
				Namespaces.NAMESPACE_GML);

		Xml.parse(content, Xml.Encoding.UTF_8, contentHandler);
		return new CapabilitiesResult(offerings, serviceIdentifiation);
	}

	public static List<Observation> processObservationResponse(
			InputStream content) throws IOException, SAXException, OWSException {
		class ObservationFactory {
			int nextDataRecordFieldIndex = 0;
			String dataRecordValues;
			String uom;
			Integer timeRecordIndex, quanityRecordIndex;
			String decimalSeparator, tokenSeparator, blockSeparator;

			public void reset() {
				dataRecordValues = null;
				uom = null;
				timeRecordIndex = quanityRecordIndex = null;
				nextDataRecordFieldIndex = 0;
				decimalSeparator = tokenSeparator = blockSeparator = null;
			}

			public void increaseDataRecordFieldIndex() {
				nextDataRecordFieldIndex++;
			}

			public Observation createObservation() {
				List<Value> values = new ArrayList<SOSResponse.Observation.Value>();

				String[] blocks = dataRecordValues.split(blockSeparator);
				for (int i = 0; i < blocks.length; i++) {
					String[] tokens = blocks[i].split(tokenSeparator);
					Date samplingTime = DateUtils
							.getDateFromISOString(tokens[timeRecordIndex]);

					float quantity = Float
							.parseFloat(tokens[quanityRecordIndex]);
					// TODO use decimalSeparator

					values.add(new Value(samplingTime, quantity));

				}
				return new Observation(values, uom);
			}
		}

		class OWSExceptionFactory {
			String code;
			String text;

			public OWSException createException() {
				return new OWSException(code, text);
			}

			public boolean hasException() {
				return code != null || text != null;
			}
		}

		final List<Observation> observations = new ArrayList<SOSResponse.Observation>();
		RootElement root = new NoRootElement();
		final ObservationFactory observationFactory = new ObservationFactory();
		final OWSExceptionFactory owsExceptionFactory = new OWSExceptionFactory();

		Element exceptionElement = root

		.getChild(Namespaces.NAMESPACE_OWS, "ExceptionReport").getChild(
				Namespaces.NAMESPACE_OWS, "Exception");
		exceptionElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				owsExceptionFactory.code = attributes.getValue("exceptionCode");
			}
		});
		exceptionElement.getChild(Namespaces.NAMESPACE_OWS, "ExceptionText")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						owsExceptionFactory.text = body;
					}
				});

		Element observation = root
				.getChild(Namespaces.NAMESPACE_OM, "ObservationCollection")
				.getChild(Namespaces.NAMESPACE_OM, "member")
				.getChild(Namespaces.NAMESPACE_OM, "Observation");
		observation.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				observationFactory.reset();
			}
		});

		// Parse result node
		Element dataArrayElement = observation.getChild(
				Namespaces.NAMESPACE_OM, "result").getChild(
				Namespaces.NAMESPACE_SWE, "DataArray");

		Element fieldElement = dataArrayElement
				.getChild(Namespaces.NAMESPACE_SWE, "elementType")
				.getChild(Namespaces.NAMESPACE_SWE, "DataRecord")
				.getChild(Namespaces.NAMESPACE_SWE, "field");
		fieldElement.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				observationFactory.increaseDataRecordFieldIndex();
			}
		});

		// Quantity field
		Element quantityElement = fieldElement.getChild(
				Namespaces.NAMESPACE_SWE, "Quantity");
		quantityElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				observationFactory.quanityRecordIndex = observationFactory.nextDataRecordFieldIndex;
			}
		});
		quantityElement.getChild(Namespaces.NAMESPACE_SWE, "uom")
				.setStartElementListener(new StartElementListener() {

					@Override
					public void start(Attributes attributes) {
						observationFactory.uom = attributes.getValue("code");
					}
				});

		// Time field
		fieldElement.getChild(Namespaces.NAMESPACE_SWE, "Time")
				.setStartElementListener(new StartElementListener() {
					@Override
					public void start(Attributes attributes) {
						observationFactory.timeRecordIndex = observationFactory.nextDataRecordFieldIndex;
					}
				});

		// Encoding
		dataArrayElement.getChild(Namespaces.NAMESPACE_SWE, "encoding")
				.getChild(Namespaces.NAMESPACE_SWE, "TextBlock")
				.setStartElementListener(new StartElementListener() {
					@Override
					public void start(Attributes attributes) {
						observationFactory.decimalSeparator = attributes
								.getValue("decimalSeparator");
						observationFactory.tokenSeparator = attributes
								.getValue("tokenSeparator");
						observationFactory.blockSeparator = attributes
								.getValue("blockSeparator");
					}
				});

		// Values
		dataArrayElement.getChild(Namespaces.NAMESPACE_SWE, "values")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						observationFactory.dataRecordValues = body;
					}
				});

		observation.setEndElementListener(new EndElementListener() {
			public void end() {
				observations.add(observationFactory.createObservation());
			}
		});

		ContentHandler contentHandler = root.getContentHandler();
		// Pretend a prefix mapping event with the GML namespace, as it
		// is not included in most responses
		contentHandler.startPrefixMapping(Namespaces.NAMESPACE_GML_PREFIX,
				Namespaces.NAMESPACE_GML);

		Xml.parse(content, Xml.Encoding.UTF_8, contentHandler);
		if (owsExceptionFactory.hasException()) {
			throw owsExceptionFactory.createException();
		}
		return observations;
	}

	public static List<FOI> processFeatureOfInterestResponse(
			InputStream content, final String serviceUrl) throws IOException,
			SAXException, OWSException {
		class FOIFactory {
			String description;
			String name;
			String id;
			float latitude, longitude;

			public FOI createFOI() {
				return new FOI(id, name, description, new GeoLocation(latitude,
						longitude), serviceUrl);
			}

			public void reset() {
				id = null;
				name = null;
				description = null;
			}
		}

		class OWSExceptionFactory {
			String code;
			String text;

			public OWSException createException() {
				return new OWSException(code, text);
			}

			public boolean hasException() {
				return code != null || text != null;
			}
		}

		final List<FOI> foiList = new ArrayList<FOI>();
		RootElement root = new NoRootElement();
		final FOIFactory foiFactory = new FOIFactory();
		final OWSExceptionFactory owsExceptionFactory = new OWSExceptionFactory();

		Element exceptionElement = root.getChild(Namespaces.NAMESPACE_OWS,
				"ExceptionReport").getChild(Namespaces.NAMESPACE_OWS,
				"Exception");
		exceptionElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				owsExceptionFactory.code = attributes.getValue("exceptionCode");
			}
		});
		exceptionElement.getChild(Namespaces.NAMESPACE_OWS, "ExceptionText")
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						owsExceptionFactory.text = body;
					}
				});

		for (Element samplingPoint : new Element[] {
				root.getChild(Namespaces.NAMESPACE_GML, "FeatureCollection")
						.getChild(Namespaces.NAMESPACE_GML, "featureMember")
						.getChild(Namespaces.NAMESPACE_SAMPLING,
								"SamplingPoint"),
				root.getChild(Namespaces.NAMESPACE_SAMPLING, "SamplingPoint") }) {
			// Parse SamplingPoint node
			samplingPoint.setStartElementListener(new StartElementListener() {
				@Override
				public void start(Attributes attributes) {
					foiFactory.reset();
					foiFactory.id = attributes.getValue(
							Namespaces.NAMESPACE_GML, "id");
				}
			});
			samplingPoint.setEndElementListener(new EndElementListener() {
				public void end() {
					foiList.add(foiFactory.createFOI());
				}
			});
			samplingPoint.requireChild(Namespaces.NAMESPACE_GML, "name")
					.setEndTextElementListener(new EndTextElementListener() {
						public void end(String body) {
							foiFactory.name = body;
						}
					});
			samplingPoint.getChild(Namespaces.NAMESPACE_GML, "description")
					.setEndTextElementListener(new EndTextElementListener() {
						public void end(String body) {
							foiFactory.description = body;
						}
					});
			samplingPoint.getChild(Namespaces.NAMESPACE_SAMPLING, "position")
					.getChild(Namespaces.NAMESPACE_GML, "Point")
					.getChild(Namespaces.NAMESPACE_GML, "pos")
					.setEndTextElementListener(new EndTextElementListener() {
						@Override
						public void end(String body) {
							String[] split = body.split(" ");
							foiFactory.latitude = Float.parseFloat(split[0]);
							foiFactory.longitude = Float.parseFloat(split[1]);
						}
					});
		}
		ContentHandler contentHandler = root.getContentHandler();
		// Pretend a prefix mapping event with the GML and SA namespace, as it
		// can not get read from th schemaLocation attributes
		contentHandler.startPrefixMapping(Namespaces.NAMESPACE_GML_PREFIX,
				Namespaces.NAMESPACE_GML);
		contentHandler.startPrefixMapping(Namespaces.NAMESPACE_SAMPLING_PREFIX,
				Namespaces.NAMESPACE_SAMPLING);

		Xml.parse(content, Xml.Encoding.UTF_8, contentHandler);
		if (owsExceptionFactory.hasException()) {
			throw owsExceptionFactory.createException();
		}
		return foiList;
	}

	private SOSResponse() {

	}

}
