/**
 * Copyright 2012 52�North Initiative for Geospatial Open Source Software GmbH
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
package org.n52.geoar.data.opensearch.data.atom.geo;

import org.n52.geoar.data.opensearch.data.BaseFeedElement;
import org.n52.geoar.data.opensearch.data.core.IBoundingBox;
import org.xml.sax.Attributes;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 *
 */
public class AtomBoundingBox extends BaseFeedElement implements IBoundingBox {

    public AtomBoundingBox(String name, String uri, Attributes attibutes) {
        super(name, uri, attibutes);
    }

    @Override
    public double getLowerLatitude() {
        String[] coords = getContent().split(" ");
        return Double.parseDouble(coords[0].trim());
    }

    @Override
    public double getLowerLongitude() {
        String[] coords = getContent().split(" ");
        return Double.parseDouble(coords[1].trim());
    }

    @Override
    public double getUpperLatitude() {
        String[] coords = getContent().split(" ");
        return Double.parseDouble(coords[2].trim());
    }

    @Override
    public double getUpperLongitude() {
        String[] coords = getContent().split(" ");
        return Double.parseDouble(coords[3].trim());
    }

    @Override
    public double[] getBBoxCoordinates() {
        String[] coords = getContent().split(" ");
        double[] values = new double[] { 
                Double.parseDouble(coords[0].trim()),
                Double.parseDouble(coords[1].trim()),
                Double.parseDouble(coords[2].trim()),
                Double.parseDouble(coords[3].trim()) };
        return values;
    }
}
