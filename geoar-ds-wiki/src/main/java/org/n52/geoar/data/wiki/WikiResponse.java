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
package org.n52.geoar.data.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class WikiResponse {

	private static String ATTRIB_TITLE = "title";
	private static String ATTRIB_ID = "id";
	private static String ATTRIB_LAT = "lat";
	private static String ATTRIB_LON = "lng";
	private static String ATTRIB_ALT = "elevation";
	private static String ATTRIB_URL = "url";

	static List<WikiResult> getWikiresultsFromResponse(InputStream content)
			throws IOException {

		List<WikiResult> resultList = new ArrayList<WikiResult>();

		BufferedReader streamReader = new BufferedReader(new InputStreamReader(
				content, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);

		try {
			JSONObject root = new JSONObject(responseStrBuilder.toString());
			JSONArray dataArray = root.getJSONArray("articles");

			for (int i = 0, length = dataArray.length(); i < length; i++) {
				JSONObject wikiObject = dataArray.getJSONObject(i);
				WikiResult wr = null;
				if (wikiObject.has(ATTRIB_TITLE) && wikiObject.has(ATTRIB_LAT)
						&& wikiObject.has(ATTRIB_LON)) {
					wr = new WikiResult(wikiObject.getString(ATTRIB_ID),
							wikiObject.getString(ATTRIB_TITLE),
							wikiObject.getDouble(ATTRIB_LAT),
							wikiObject.getDouble(ATTRIB_LON));
					
					// FIXME make altitude still a parameter
//					if (wikiObject.has(ATTRIB_ALT))
//						wr.setAltitude(wikiObject.getInt(ATTRIB_ALT));

					/** requesting mobile version of website */
					if (wikiObject.has(ATTRIB_URL)) {
						String url = wikiObject.getString(ATTRIB_URL);
						int splitIndex = url.indexOf('.');
						String first = url.substring(0, splitIndex);
						String last = url.substring(splitIndex + 1,
								url.length());
						wr.setUrl(first + ".m." + last);
					}
					resultList.add(wr);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return resultList;
	}
}
