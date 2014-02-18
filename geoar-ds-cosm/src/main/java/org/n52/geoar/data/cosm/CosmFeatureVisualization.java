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
package org.n52.geoar.data.cosm;

import org.n52.geoar.data.cosm.CosmFeed;
import org.n52.geoar.newdata.Annotations.PluginContext;
import org.n52.geoar.newdata.SpatialEntity;
import org.n52.geoar.newdata.Visualization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class CosmFeatureVisualization implements
		Visualization.FeatureVisualization {
	
	private static class WikiWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
	}
	
	private static WikiWebViewClient WEBVIEWINSTANCE = new WikiWebViewClient();
	
	private class ViewHolder {
		public TextView wikiTitle;
		public WebView wikiWebView;
	}
	
	@PluginContext
	private Context context;

	@Override
	public String getTitle(SpatialEntity entity) {
		return ((CosmFeed) entity).getTitle();
	}

	@Override
	public String getDescription(SpatialEntity entity) {
		return ""; //((WikiResult) entity).getId();
	}

	@Override
	public View getFeatureView(SpatialEntity entity, View convertView,
			ViewGroup parentView, Context activityContext) {
		ViewHolder viewHolder;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.featureview, null);
			
			viewHolder = new ViewHolder();
			WebView wikiWebView = (WebView) convertView.findViewById(R.id.wikiWebView);
			wikiWebView.getSettings().setJavaScriptEnabled(true);
			wikiWebView.getSettings().setUseWideViewPort(true);
			wikiWebView.setWebViewClient(WEBVIEWINSTANCE);
			viewHolder.wikiWebView = wikiWebView;
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(entity instanceof CosmFeed){
			CosmFeed cosmFeed = (CosmFeed) entity;
			viewHolder.wikiWebView.loadUrl("https://cosm.com/feeds/" + cosmFeed.getId());
		}
		
		return convertView;
	}

	@Override
	public View getFeatureDetailView(SpatialEntity entity, View convertView,
			ViewGroup parentView, Context activityContext) {
		ViewHolder viewHolder;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.feature_detail_view, null);
			
			viewHolder = new ViewHolder();
			viewHolder.wikiTitle = (TextView) convertView.findViewById(R.id.textViewWikiTitle);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(entity instanceof CosmFeed){
			CosmFeed wikiResult = (CosmFeed) entity;
			viewHolder.wikiTitle.setText(wikiResult.getTitle());
		}
		return convertView;
	}
}
