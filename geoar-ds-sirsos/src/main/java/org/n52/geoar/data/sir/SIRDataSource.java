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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.n52.geoar.data.R;
import org.n52.geoar.newdata.Annotations;
import org.n52.geoar.newdata.Annotations.DefaultInstances;
import org.n52.geoar.newdata.Annotations.DefaultSetting;
import org.n52.geoar.newdata.Annotations.DefaultSettingsSet;
import org.n52.geoar.newdata.Annotations.NameCallback;
import org.n52.geoar.newdata.Annotations.Setting;
import org.n52.geoar.newdata.Annotations.Settings.Name;
import org.n52.geoar.newdata.Annotations.Settings.NotNull;
import org.n52.geoar.newdata.Annotations.SharedHttpClient;
import org.n52.geoar.newdata.Annotations.SupportedVisualization;
import org.n52.geoar.newdata.DataSource;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.utils.GeoLocationRect;

import com.vividsolutions.jts.geom.Geometry;

@Annotations.DataSource(name = @Name(resId = R.string.sir_data_source_title), cacheZoomLevel = 8, minZoomLevel = 7, minReloadInterval = -1)
@SupportedVisualization(visualizationClasses = { SIRMapVisualization.class,
		SIRARVisualization.class })
// TODO externalize URL
@DefaultInstances(@DefaultSettingsSet(@DefaultSetting(name = "serverUrl", value = "http://geoviqua.dev.52north.org/SIR/sir")))
public class SIRDataSource implements DataSource<SIRFilter> {

	@SharedHttpClient
	private HttpClient mHttpClient;

	@Setting
	@NotNull
	@Name(resId = R.string.sir_post_endpoint)
	// TODO externalize URL
	private String serverUrl = "http://geoviqua.dev.52north.org/SIR/sir";

	@Override
	public List<? extends SpatialEntity2<? extends Geometry>> getMeasurements(SIRFilter filter)
			throws Exception {
	    org.achartengine.model.Point p = new org.achartengine.model.Point();
		return searchSensorRequest(filter.getBoundingBox());
		// TODO handle error more specifically
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@NameCallback
	public String getName() {
		try {
			return new URL(serverUrl).getHost();
		} catch (MalformedURLException e) {
			return serverUrl;
		}
	}

	private List<SIRResult> searchSensorRequest(GeoLocationRect bbox)
			throws Exception {
		HttpPost httpPost = new HttpPost(serverUrl);
		httpPost.setHeader("Content-Type", "text/xml");

		ByteArrayEntity requestEntity = new ByteArrayEntity(SIRRequest
				.createSearchSensorRequestXML(bbox).toByteArray());

		httpPost.setEntity(requestEntity);

		HttpResponse response = mHttpClient.execute(httpPost);
		HttpEntity responseEntity = response.getEntity();
		InputStream content = responseEntity.getContent();
		List<SIRResult> resultsFromResponse = SIRResponse
				.getResultsFromResponse(content);
		content.close();
		return resultsFromResponse;
	}

}
