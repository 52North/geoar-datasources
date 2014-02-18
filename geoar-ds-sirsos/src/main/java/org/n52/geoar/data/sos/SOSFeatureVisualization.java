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
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.geoar.data.R;
import org.n52.geoar.data.sos.SOSResponse.CapabilitiesResult;
import org.n52.geoar.data.sos.SOSResponse.Observation;
import org.n52.geoar.data.sos.SOSResponse.Offering;
import org.n52.geoar.data.sos.view.SOSObservationView;
import org.n52.geoar.data.sos.view.SOSObservationView.ObservationCallable;
import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.Annotations.SharedHttpClient;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.newdata.Visualization;

import com.vividsolutions.jts.geom.Geometry;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

public class SOSFeatureVisualization implements
		Visualization.FeatureVisualization {

	private class ViewHolder {
		FOI featureOfInterest;
		TextView textViewServiceSensorId;
		TextView textViewDescription;
		public TextView textViewName;
		Callable<CapabilitiesResult> capabilitiesCallable = new Callable<CapabilitiesResult>() {
			@Override
			public CapabilitiesResult call() throws Exception {
				return SOSUtils.getCapabilities(mHttpClient,
						featureOfInterest.serviceUrl).get();
			}
		};
		ObservationCallable observationCallable = new ObservationCallable() {
			@Override
			protected List<Observation> call(Offering offering, Date begin,
					Date end) throws Exception {
				HttpPost httpPost = new HttpPost(featureOfInterest.serviceUrl);
				httpPost.setHeader("Content-Type", "text/xml");

				ByteArrayEntity requestEntity = new ByteArrayEntity(SOSRequest
						.createObservationRequestXML(offering, null, begin,
								end, featureOfInterest).toByteArray());
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
		public SOSObservationView sosObservationView;

	}

	@PluginContext
	private Context context;

	@SharedHttpClient
	private DefaultHttpClient mHttpClient;

	@Override
	public String getTitle(SpatialEntity2<? extends Geometry> entity) {
		if (entity instanceof FOI)
			return "Feature " + ((FOI) entity).id;
		return "";
	}

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
					R.layout.sos_featureview, null);
			viewHolder = new ViewHolder();

			viewHolder.textViewServiceSensorId = (TextView) convertView
					.findViewById(R.id.textViewServiceSensorId);
			viewHolder.textViewDescription = (TextView) convertView
					.findViewById(R.id.textViewDescription);
			viewHolder.textViewName = (TextView) convertView
					.findViewById(R.id.textViewName);

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
			tabHost.addTab(tabHost.newTabSpec("FOI").setIndicator("Feature")
					.setContent(R.id.scrollViewFOI));
			tabHost.addTab(tabHost.newTabSpec("SOS")
					.setIndicator("Observations").setContent(R.id.layoutSOS));

			tabHost.setCurrentTab(0);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (entity instanceof FOI) {
			viewHolder.featureOfInterest = (FOI) entity;
			viewHolder.textViewName.setText(viewHolder.featureOfInterest.name);
			if (viewHolder.featureOfInterest.description != null) {
				viewHolder.textViewDescription.setText(Html
						.fromHtml(viewHolder.featureOfInterest.description));
			}
			viewHolder.textViewServiceSensorId
					.setText(viewHolder.featureOfInterest.id);
		}
		return convertView;
	}

	@Override
	public View getFeatureDetailView(SpatialEntity2<? extends Geometry> entity, View convertView,
			ViewGroup parentView, Context activityContext) {
		// TODO Auto-generated method stub
		return null;
	}

}
