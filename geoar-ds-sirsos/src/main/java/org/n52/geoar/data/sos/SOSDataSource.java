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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.n52.geoar.data.R;
import org.n52.geoar.data.sos.SOSResponse.CapabilitiesResult;
import org.n52.geoar.data.sos.SOSResponse.OWSException;
import org.n52.geoar.data.sos.SOSResponse.ServiceIdentifiation;
import org.n52.geoar.newdata.Annotations;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.newdata.Annotations.DefaultInstances;
import org.n52.geoar.newdata.Annotations.DefaultSetting;
import org.n52.geoar.newdata.Annotations.DefaultSettingsSet;
import org.n52.geoar.newdata.Annotations.NameCallback;
import org.n52.geoar.newdata.Annotations.PostConstruct;
import org.n52.geoar.newdata.Annotations.PostSettingsChanged;
import org.n52.geoar.newdata.Annotations.Setting;
import org.n52.geoar.newdata.Annotations.Settings.Name;
import org.n52.geoar.newdata.Annotations.Settings.NotNull;
import org.n52.geoar.newdata.Annotations.SharedHttpClient;
import org.n52.geoar.newdata.Annotations.SupportedVisualization;
import org.n52.geoar.newdata.DataSource;
import org.n52.geoar.utils.GeoLocationRect;

import com.vividsolutions.jts.geom.Geometry;

@Annotations.DataSource(name = @Name(resId = R.string.sos_data_source_title), cacheZoomLevel = 8, minZoomLevel = 7, minReloadInterval = -1)
@SupportedVisualization(visualizationClasses = { SOSMapVisualization.class,
		SOSARVisualization.class })
@DefaultInstances({
		@DefaultSettingsSet({ @DefaultSetting(name = "serviceUrl", value = "http://sensorweb.demo.52north.org/52nSOSv3.2.1/sos") }),
		@DefaultSettingsSet({ @DefaultSetting(name = "serviceUrl", value = "http://sensorweb.demo.52north.org/PegelOnlineSOSv2.1/sos") }),
		@DefaultSettingsSet({ @DefaultSetting(name = "serviceUrl", value = "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos") }) })
public class SOSDataSource implements DataSource<SOSFilter> {

	@Setting
	@NotNull
	@Name(resId = R.string.sos_post_endpoint)
	private String serviceUrl = null;

	@SharedHttpClient
	private HttpClient mHttpClient;

	private Boolean useEpsgVersion = null;

	ExecutorService capabilitiesExecutor = Executors.newSingleThreadExecutor();

	private Future<ServiceIdentifiation> capabilitiesFuture;

	@Override
	public List<? extends SpatialEntity2<? extends Geometry>> getMeasurements(SOSFilter filter)
			throws Exception {
		return getFeatureOfInterest(filter.getBoundingBox());
		// TODO handle error more specifically
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	private ServiceIdentifiation getServiceIdentification() {
		try {
			return capabilitiesFuture.get();
		} catch (InterruptedException e) {
			capabilitiesFuture = null;
		} catch (ExecutionException e) {
			capabilitiesFuture = null;
		}
		return null;
	}

	@NameCallback
	public String getName() {
		if (capabilitiesFuture != null && capabilitiesFuture.isDone()
				&& getServiceIdentification() != null
				&& getServiceIdentification().getTitle() != null) {
			return getServiceIdentification().getTitle();
		}
		if (serviceUrl != null) {
			try {
				return new URL(serviceUrl).getHost();
			} catch (MalformedURLException e) {

			}
		}
		return serviceUrl;
	}

	@PostSettingsChanged
	public void reloadCapabilities() {
		if (capabilitiesFuture != null && capabilitiesFuture.isDone()
				&& getServiceIdentification() != null
				&& getServiceIdentification().serviceUrl.equals(serviceUrl)) {
			return;
		}

		// TODO stop other executions

		capabilitiesFuture = null;
		if (mHttpClient != null) {
			// use mHttpClient to ensure that data source is really
			// constructed/got injected
			init();
		}
	}

	@PostConstruct
	public void init() {
		if (serviceUrl == null
				|| (capabilitiesFuture != null && capabilitiesFuture.isDone())) {
			return;
		}

		// TODO maybe use SOSUtils

		Callable<ServiceIdentifiation> capabilitiesCallable = new Callable<ServiceIdentifiation>() {
			@Override
			public ServiceIdentifiation call() throws Exception {
				try {
					HttpPost httpPost = new HttpPost(serviceUrl);
					httpPost.setHeader("Content-Type", "text/xml");
					httpPost.setHeader("Accept", "text/xml");
					ByteArrayEntity requestEntity = new ByteArrayEntity(
							SOSRequest.createCapabilitiesRequestXML()
									.toByteArray());
					httpPost.setEntity(requestEntity);

					HttpResponse response = mHttpClient.execute(httpPost);
					HttpEntity responseEntity = response.getEntity();
					InputStream content = responseEntity.getContent();
					CapabilitiesResult result = SOSResponse
							.processCapabilitiesResponse(content, serviceUrl);
					content.close();
					return result.getServiceIdentifiation();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}
		};
		capabilitiesFuture = capabilitiesExecutor.submit(capabilitiesCallable);
	}

	private List<FOI> getFeatureOfInterest(GeoLocationRect bbox)
			throws Exception {
		HttpPost httpPost = new HttpPost(serviceUrl);
		httpPost.setHeader("Content-Type", "text/xml");
		httpPost.setHeader("Accept", "text/xml");

		boolean useEpsgVer = useEpsgVersion != null ? useEpsgVersion
				.booleanValue() : true;
		// Try with epsg version first or use previous decision
		ByteArrayEntity requestEntity = new ByteArrayEntity(SOSRequest
				.createFeatureOfInterestRequestXML(bbox, useEpsgVer)
				.toByteArray());
		httpPost.setEntity(requestEntity);

		HttpResponse response = mHttpClient.execute(httpPost);
		HttpEntity responseEntity = response.getEntity();
		List<FOI> fois = null;
		InputStream content = responseEntity.getContent();
		try {
			fois = SOSResponse.processFeatureOfInterestResponse(content,
					serviceUrl);
		} catch (OWSException e) {
			if (useEpsgVer
					&& e.getTitle().equals("InvalidParameterValue")
					&& e.getMessage().contains(
							"schema:urn:ogc:def:crs:EPSG:number")) {
				// Used epsg version and received ExceptionReport about srs name
				// convention -> use fallback and try again
				useEpsgVersion = false;
				fois = getFeatureOfInterest(bbox);
			} else {
				throw e;
			}
		} finally {
			content.close();
		}
		// If request succeeded
		if (useEpsgVersion == null) {
			useEpsgVersion = true;
		}
		return fois;
	}
}
