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

import java.text.SimpleDateFormat;

import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.newdata.Visualization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class NoiseFeatureVisualization implements
		Visualization.FeatureVisualization {

	private class ViewHolder {
		TextView textViewNoise;
		public TextView textViewProvider;
		public TextView textViewMoral;
		public TextView textViewTime;
	}

	@PluginContext
	private Context context;

	@Override
	public String getTitle(SpatialEntity2<? extends Geometry> entity) {
		return null;
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.featureview, null);
			viewHolder = new ViewHolder();

			viewHolder.textViewNoise = (TextView) convertView
					.findViewById(R.id.textViewNoise);
			viewHolder.textViewProvider = (TextView) convertView
					.findViewById(R.id.textViewLocationProvider);
			viewHolder.textViewMoral = (TextView) convertView
					.findViewById(R.id.textViewMoral);
			viewHolder.textViewTime = (TextView) convertView
					.findViewById(R.id.textViewTime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (entity instanceof NoiseMeasurement) {
			NoiseMeasurement noiseMeasurement = (NoiseMeasurement) entity;
			viewHolder.textViewNoise.setText(noiseMeasurement.getValue()
					+ " dB");
			viewHolder.textViewProvider.setText(noiseMeasurement.getProvider()
					.toString());
			viewHolder.textViewMoral
					.setText(noiseMeasurement.getMoral() + " %");
			viewHolder.textViewTime.setText(SimpleDateFormat
					.getDateTimeInstance().format(
							noiseMeasurement.getTime().getTime()));
		}
		return convertView;
	}

	@Override
	public View getFeatureDetailView(SpatialEntity2<? extends Geometry> entity, View convertView,
			ViewGroup parentView, Context activityContext) {
		ViewHolder viewHolder;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.feature_detail_view, null);
			viewHolder = new ViewHolder();

			viewHolder.textViewNoise = (TextView) convertView
					.findViewById(R.id.textViewNoise);
			viewHolder.textViewProvider = (TextView) convertView
					.findViewById(R.id.textViewProvider);
			viewHolder.textViewMoral = (TextView) convertView
					.findViewById(R.id.textViewMoral);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (entity instanceof NoiseMeasurement) {
			NoiseMeasurement noiseMeasurement = (NoiseMeasurement) entity;
			viewHolder.textViewNoise.setText(((int) (noiseMeasurement
					.getValue() * 100)) / 100f + " dB");
			viewHolder.textViewProvider.setText(noiseMeasurement.getProvider()
					.toString());
			viewHolder.textViewMoral
					.setText(noiseMeasurement.getMoral() + " %");
		}

		return convertView;
	}
}
