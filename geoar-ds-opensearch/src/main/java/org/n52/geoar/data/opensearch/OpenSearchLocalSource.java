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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.n52.geoar.data.MeasureParserException;
import org.n52.geoar.data.opensearch.OpenSearchMeasurement.LocationProvider;
import org.n52.geoar.data.opensearch.data.BaseFeedParser;
import org.n52.geoar.data.opensearch.data.core.IFeed;
import org.n52.geoar.data.opensearch.data.descriptiondocument.OpenSearchDescriptionDocument;
import org.n52.geoar.newdata.Annotations;
import org.n52.geoar.newdata.Annotations.Settings.Name;
import org.n52.geoar.newdata.Annotations.SharedHttpClient;
import org.n52.geoar.newdata.Annotations.SupportedVisualization;
import org.n52.geoar.newdata.DataSource;
import org.n52.geoar.newdata.SpatialEntity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Environment;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 */
@Annotations.DataSource(name = @Name("OpenSearch"), cacheZoomLevel = 0, minReloadInterval = -1, description = "Example for a description")
@SupportedVisualization(visualizationClasses = {
        OpenSearchMapVisualization.class, OpenSearchARVisualization.class })
public class OpenSearchLocalSource implements DataSource<OpenSearchFilter> {

    private static String noiseDroidMeasurementsPath = Environment
            .getExternalStorageDirectory()
            + "/Android/data/de.noisedroid/measures.xml";

    private static final String LINK = "http://api.flickr.com/services/feeds/photos_public.gne";
    private static final String DESCRIPTION_DOCUMENT_URL = "http://184.73.174.89/gi-cat-StP/services/opensearchgeo?getDescriptionDocument";

    private static OpenSearchDescriptionDocument openSearch;


    @SharedHttpClient
    private HttpClient httpClient;


    @Override
    public List<? extends SpatialEntity> getMeasurements(OpenSearchFilter filter) {

        HttpGet httpGet = new HttpGet("http://api.flickr.com/services/feeds/photos_public.gne");
        
            HttpResponse response;
            try {
                response = httpClient.execute(httpGet);
                InputStream input = response.getEntity().getContent();
                BaseFeedParser parser = new BaseFeedParser();
                IFeed feed = parser.parse(input);
                
                feed.getEntrys();
            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

//        if (openSearch == null) {
//            HttpGet httpGet = new HttpGet(DESCRIPTION_DOCUMENT_URL);
//            try {
//                HttpResponse response = httpClient.execute(httpGet);
//                InputStream inputstream = response.getEntity().getContent();
//
//                DocumentBuilderFactory factory = DocumentBuilderFactory
//                        .newInstance();
//                factory.setNamespaceAware(true);
//                DocumentBuilder documentBuilder = factory.newDocumentBuilder();
//
//                openSearch = new OpenSearchDescriptionDocument(
//                        documentBuilder.parse(inputstream, null));
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (SAXException e) {
//                e.printStackTrace();
//            } catch (ParserConfigurationException e) {
//                e.printStackTrace();
//            }
//        }
        try {
//            feedwarmer.warmFeed(new URL(
//                    "http://api.flickr.com/services/feeds/photos_public.gne"),
//                    "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
        // try {
        //
        //
        // Reader reader = new FileReader(noiseDroidMeasurementsPath);
        //
        // List<OpenSearchMeasurement> measurements =
        // getMeasuresFromResponse(reader);
        //
        // Iterator<OpenSearchMeasurement> iterator = measurements.iterator();
        // while (iterator.hasNext()) {
        // if (!filter.filter(iterator.next())) {
        // iterator.remove();
        // }
        // }
        //
        // return measurements;
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // return null;
        // } catch (RequestException e) {
        // e.printStackTrace();
        // return null;
        // }

    }

    public boolean isAvailable() {
        // NoiseDroid data is available if specific file exists on sd card
        return new File(noiseDroidMeasurementsPath).canRead();
    }

    public static List<OpenSearchMeasurement> getMeasuresFromResponse(
            Reader reader) throws RequestException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(reader);
            return getMeasuresFromParser(parser);
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }

    private static List<OpenSearchMeasurement> getMeasuresFromParser(
            XmlPullParser parser) throws RequestException {
        try {
            List<OpenSearchMeasurement> measureResultList = new ArrayList<OpenSearchMeasurement>();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("Measure")) {
                        measureResultList.add(getMeasureFromXML(parser));
                    } else if (parser.getName().equals("Error")) {
                        return null;
                    }
                }
                eventType = parser.next();
            }
            parser.setInput(null);
            return measureResultList;
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }

    public static OpenSearchMeasurement getMeasureFromXML(XmlPullParser parser)
            throws MeasureParserException {
        try {

            Float value = null, accuracy = null, moral = null;
            Double latitude = null, longitude = null;
            LocationProvider provider = null;
            Calendar time = null;

            while (true) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("Time")) {
                        time = stringToCalendar(parser.getAttributeValue(null,
                                "value"));
                    } else if (parser.getName().equals("LocationMeasure")) {
                        longitude = Double.parseDouble(parser
                                .getAttributeValue(null, "longitude"));
                        latitude = Double.parseDouble(parser.getAttributeValue(
                                null, "latitude"));
                        accuracy = Float.parseFloat(parser.getAttributeValue(
                                null, "accuracy"));
                        provider = LocationProvider.fromString(parser
                                .getAttributeValue(null, "provider"));
                    } else if (parser.getName().equals("NoiseMeasure")) {
                        value = Float.parseFloat(parser.getAttributeValue(null,
                                "value"));
                    } else if (parser.getName().equals("Moral")) {
                        moral = Float.parseFloat(parser.getAttributeValue(null,
                                "value"));
                    }

                }

                // Abbruch
                if (parser.getEventType() == XmlPullParser.END_TAG
                        && parser.getName().equals("Measure")) {
                    break;
                }

                parser.next();
            }
            OpenSearchMeasurement m = new OpenSearchMeasurement(latitude,
                    longitude, value);
            m.setTime(time);
            m.setProvider(provider);
            m.setLocationAccuracy(accuracy);
            m.setMoral(moral);
            return m;
        } catch (Exception e) {
            throw new MeasureParserException(e.getMessage());
        }
    }

    public static Calendar stringToCalendar(String timeString) {
        Locale locale = Locale.GERMAN;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                locale);
        try {
            Date d = format.parse(timeString);
            Calendar c = new GregorianCalendar();
            c.setTime(d);

            return c;
        } catch (ParseException e) {
            return null;
        }
    }

}
