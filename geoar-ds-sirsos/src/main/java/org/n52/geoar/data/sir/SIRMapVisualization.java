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

import org.n52.geoar.data.R;
import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.Annotations.PostConstruct;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.newdata.Visualization.MapVisualization;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.vividsolutions.jts.geom.Geometry;

public class SIRMapVisualization extends SIRFeatureVisualization implements
		MapVisualization.ItemVisualization {

	private Drawable defaultDrawable;

	@PluginContext
	private Context pluginContext;

	@Override
	public Drawable getDrawableForEntity(SpatialEntity2<? extends Geometry> entity) {
		return defaultDrawable;
	}

	@PostConstruct
	public void init() {
		defaultDrawable = pluginContext.getResources().getDrawable(
				R.drawable.mapmarker_blue);

		int w = defaultDrawable.getIntrinsicWidth();
		int h = defaultDrawable.getIntrinsicHeight();

		defaultDrawable.setBounds(-w / 2, -h / 2, w / 2, h / 2);
	}
}
