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

import org.n52.geoar.data.R;
import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.RenderFeatureFactory;
import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.newdata.Visualization.ARVisualization;
import org.n52.geoar.newdata.vis.DataSourceVisualization.DataSourceVisualizationCanvas;
import org.n52.geoar.newdata.vis.DataSourceVisualization.DataSourceVisualizationGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class SOSARVisualization extends SOSFeatureVisualization implements
		ARVisualization.ItemVisualization {

	static Paint circlePaint = new Paint();
	static Bitmap bitmap;
	static int cx, cy;
	
	@PluginContext
	private Context context;
	
	@Override
	public DataSourceVisualizationGL getEntityVisualization(
	        SpatialEntity2<? extends Geometry> entity, RenderFeatureFactory fac) {
		return fac.createCube();
	}

	@Override
	public DataSourceVisualizationCanvas getEntityVisualization(
	        SpatialEntity2<? extends Geometry> entity) {
		if(bitmap == null){
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.deepbluecircle2200x200);
			cx = bitmap.getWidth()/2;
			cy = bitmap.getHeight()/2;
		}
		return new DataSourceVisualizationCanvas() {
			@Override
			public void onRender(float dx, float dy, Canvas canvas) {
				canvas.drawBitmap(bitmap, dx-cx, dy-cy, null);
			}
		};
	}
}
