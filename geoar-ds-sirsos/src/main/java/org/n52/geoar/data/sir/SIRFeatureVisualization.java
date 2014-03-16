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
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.geoar.data.R;
import org.n52.geoar.data.sos.SOSRequest;
import org.n52.geoar.data.sos.SOSResponse;
import org.n52.geoar.data.sos.SOSResponse.CapabilitiesResult;
import org.n52.geoar.data.sos.SOSResponse.Observation;
import org.n52.geoar.data.sos.SOSResponse.Offering;
import org.n52.geoar.data.sos.SOSUtils;
import org.n52.geoar.data.sos.view.SOSObservationView;
import org.n52.geoar.data.sos.view.SOSObservationView.ObservationCallable;
import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.Annotations.SharedHttpClient;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.newdata.Visualization;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.vividsolutions.jts.geom.Geometry;

public class SIRFeatureVisualization implements
		Visualization.FeatureVisualization {

	private class ViewHolder {
		String serviceUrl;
		TextView textViewUrl;
		TextView textViewType;
		TextView textViewServiceSensorId;
		TextView textViewDescription;
		SOSObservationView sosObservationView;
		String serviceSensorId;
		Callable<CapabilitiesResult> capabilitiesCallable = new Callable<CapabilitiesResult>() {
			@Override
			public CapabilitiesResult call() throws Exception {
				return SOSUtils.getCapabilities(mHttpClient, serviceUrl).get();
			}
		};
		ObservationCallable observationCallable = new ObservationCallable() {
			@Override
			protected List<Observation> call(Offering offering, Date begin,
					Date end) throws Exception {
				HttpPost httpPost = new HttpPost(serviceUrl);
				httpPost.setHeader("Content-Type", "text/xml");

				ByteArrayEntity requestEntity = new ByteArrayEntity(SOSRequest
						.createObservationRequestXML(offering, serviceSensorId,
								begin, end, null).toByteArray());
				httpPost.setEntity(requestEntity);

				HttpResponse response = mHttpClient.execute(httpPost);
				HttpEntity responseEntity = response.getEntity();
				InputStream content = responseEntity.getContent();
				List<Observation> result = SOSResponse
						.processObservationResponse(content);
				content.close();
				return result;
			}
		};

	}

	@PluginContext
	private Context pluginContext;

	@SharedHttpClient
	private DefaultHttpClient mHttpClient;

	@Override
	public String getDescription(SpatialEntity2<? extends Geometry> entity) {
		return null;
	}

	@Override
	public View getFeatureView(SpatialEntity2<? extends Geometry> entity, View convertView,
			ViewGroup parentView, Context activityContext) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(activityContext).inflate(
					R.layout.featureview, null);
			viewHolder = new ViewHolder();

			viewHolder.textViewUrl = (TextView) convertView
					.findViewById(R.id.textViewUrl);
			viewHolder.textViewType = (TextView) convertView
					.findViewById(R.id.textViewType);
			viewHolder.textViewServiceSensorId = (TextView) convertView
					.findViewById(R.id.textViewServiceSensorId);
			viewHolder.textViewDescription = (TextView) convertView
					.findViewById(R.id.textViewDescription);

			viewHolder.sosObservationView = (SOSObservationView) convertView
					.findViewById(R.id.sosObservationView);
			viewHolder.sosObservationView
					.setCapabilitiesCallable(viewHolder.capabilitiesCallable);
			viewHolder.sosObservationView
					.setObservationCallable(viewHolder.observationCallable);
			viewHolder.sosObservationView.setActivityContext(activityContext);

			TabHost tabHost = (TabHost) convertView
					.findViewById(android.R.id.tabhost);
			tabHost.setup();
			tabHost.addTab(tabHost.newTabSpec("SIR").setIndicator("Metadata")
					.setContent(R.id.scrollViewSIRResult));
			tabHost.addTab(tabHost.newTabSpec("SOS")
					.setIndicator("Observations").setContent(R.id.layoutSOS));

			tabHost.setCurrentTab(0);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (entity instanceof SIRResult) {
			SIRResult sirResult = (SIRResult) entity;
			viewHolder.textViewDescription.setText(Html
					.fromHtml(sirResult.description));
			viewHolder.textViewServiceSensorId
					.setText(sirResult.serviceSensorId);
			viewHolder.textViewType.setText(sirResult.serviceType);
			viewHolder.textViewUrl.setText(sirResult.serviceUrl);

			viewHolder.serviceSensorId = sirResult.serviceSensorId;
			viewHolder.serviceUrl = sirResult.serviceUrl;

		}
		return convertView;
	}

	@Override
	public String getTitle(SpatialEntity2<? extends Geometry> entity) {
		if (entity instanceof SIRResult)
			return "Sensor " + ((SIRResult) entity).getSirSensorId();
		return "";
	}

	@Override
	public View getFeatureDetailView(SpatialEntity2<? extends Geometry> entity, View convertView,
			ViewGroup parentView, Context activityContext) {
		ViewHolder viewHolder;

		if (convertView == null) {
			// convertView = LayoutInflater.from(pluginContext).inflate(
			// R.layout.featureview, null);
		}
		return null;
	}

}
