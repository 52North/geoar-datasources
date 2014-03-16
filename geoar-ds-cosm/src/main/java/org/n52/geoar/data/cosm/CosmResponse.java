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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CosmResponse {

	private static String ATTRIB_ID = "id";
	private static String ATTRIB_TITLE = "title";
	private static String ATTRIB_FEED = "feed";
	private static String ATTRIB_STATUS = "status";
	private static String ATTRIB_DESCRIPTION = "description";

	private static String ATTRIB_UPDATED = "updated";
	private static String ATTRIB_CREATED = "created";
	private static String ATTRIB_CREATOR = "creator";

	private static String ATTRIB_VERSION = "version";

	private static String ARRAY_RESULTS = "results";
	private static String ARRAY_DATASTREAMS = "datastreams";

	private static String OBJECT_LOCATION = "location";
	private static String ATTRIB_LAT = "lat";
	private static String ATTRIB_LON = "lon";

	static List<CosmFeed> getCosmFeedsFromResponse(InputStream content)
			throws IOException {

		List<CosmFeed> resultList = new ArrayList<CosmFeed>();

		BufferedReader streamReader = new BufferedReader(new InputStreamReader(
				content, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);

		try {
			JSONObject root = new JSONObject(responseStrBuilder.toString());
			JSONArray dataArray = root.getJSONArray(ARRAY_RESULTS);

			for (int i = 0, length = dataArray.length(); i < length; i++) {
				JSONObject cosmObject = dataArray.getJSONObject(i);
				CosmFeed wr = null;
				// if(wikiObject.has(name))

				JSONObject cosmObjectLocation = cosmObject
						.getJSONObject(OBJECT_LOCATION);

				CosmFeed.Builder feedBuilder = new CosmFeed.Builder(
						cosmObject.getString(ATTRIB_ID),
						cosmObject.getString(ATTRIB_TITLE),
						cosmObjectLocation.getDouble(ATTRIB_LAT),
						cosmObjectLocation.getDouble(ATTRIB_LON))
						.setFeedUrl(cosmObject.getString(ATTRIB_FEED))
						.setLastUpdated(cosmObject.getString(ATTRIB_UPDATED))
						.setCreated(cosmObject.getString(ATTRIB_CREATED))
						.setCreator(cosmObject.getString(ATTRIB_CREATOR));
				
				if(cosmObject.has(ATTRIB_STATUS))
					feedBuilder.setStatus(cosmObject.getString(ATTRIB_STATUS));
				
				if(cosmObject.has(ATTRIB_DESCRIPTION))
					feedBuilder.setDescription(cosmObject.getString(ATTRIB_DESCRIPTION));
				
				resultList.add(feedBuilder.build());
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return resultList;
	}
}
