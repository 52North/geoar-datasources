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
package org.n52.geoar.data.opensearch;

import java.util.concurrent.Callable;

import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.RenderFeatureFactory;
import org.n52.geoar.newdata.SpatialEntity;
import org.n52.geoar.newdata.Visualization.ARVisualization;
import org.n52.geoar.newdata.vis.DataSourceVisualization.DataSourceVisualizationCanvas;
import org.n52.geoar.newdata.vis.DataSourceVisualization.DataSourceVisualizationGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class OpenSearchARVisualization extends OpenSearchFeatureVisualization implements
		ARVisualization.ItemVisualization {
	
	static Bitmap bitmap;	
	static int cx, cy;

	@PluginContext
	private Context context;

	private Callable<Bitmap> textureCallback = new Callable<Bitmap>() {
		@Override
		public Bitmap call() throws Exception {
			return BitmapFactory.decodeResource(context.getResources(),
					R.drawable.noisedroid);
		}
	};

	@Override
	public DataSourceVisualizationGL getEntityVisualization(
			SpatialEntity entity, RenderFeatureFactory fac) {
		DataSourceVisualizationGL cube = fac.createCube();
		cube.setTextureCallback(textureCallback);
		cube.enableBlending(false, 1.0f);
		return cube;
	}

	@Override
	public DataSourceVisualizationCanvas getEntityVisualization(
			SpatialEntity entity) {
		if(bitmap == null){
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.redcircle200x200);
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
