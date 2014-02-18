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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.n52.geoar.data.sos.SOSResponse.CapabilitiesResult;

public class SOSUtils {

	private static class CapabilitiesRequestCallable implements
			Callable<CapabilitiesResult> {
		HttpClient httpClient;
		String serverUrl;

		public CapabilitiesRequestCallable(HttpClient httpClient,
				String serverUrl) {
			this.httpClient = httpClient;
			this.serverUrl = serverUrl;
		}

		@Override
		public CapabilitiesResult call() throws Exception {
			if (capabilitiesCache.containsKey(serverUrl)) {
				return capabilitiesCache.get(serverUrl);
			}

			try {
				HttpPost httpPost = new HttpPost(serverUrl);
				httpPost.setHeader("Content-Type", "text/xml");

				ByteArrayEntity requestEntity = new ByteArrayEntity(SOSRequest
						.createCapabilitiesRequestXML().toByteArray());
				httpPost.setEntity(requestEntity);

				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity responseEntity = response.getEntity();
				InputStream content = responseEntity.getContent();
				CapabilitiesResult result = SOSResponse
						.processCapabilitiesResponse(content, serverUrl);
				content.close();
				capabilitiesCache.put(serverUrl, result);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private static Map<String, CapabilitiesResult> capabilitiesCache = new HashMap<String, CapabilitiesResult>();
	private static ExecutorService executor = Executors.newCachedThreadPool();

	public static Future<CapabilitiesResult> getCapabilities(
			HttpClient httpClient, String severUrl) {
		return executor.submit(new CapabilitiesRequestCallable(httpClient,
				severUrl));
	}
}
