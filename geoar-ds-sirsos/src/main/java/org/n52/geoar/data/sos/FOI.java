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

import org.n52.geoar.newdata.SpatialEntity2;
import org.n52.geoar.utils.GeoLocation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * 
 * @author Arne de Wall <a.dewall@52North.org>
 * 
 * @param <G>
 * @param <Geometry>
 */
public class FOI extends SpatialEntity2<Point> {

    private static final long serialVersionUID = 1L;
    private static final GeometryFactory fac = new GeometryFactory();
    String id;
    String name;
    String description;
    String serviceUrl;

    public FOI(String id, String name, String description,
            GeoLocation geoLocation, String serviceUrl) {
        super(fac.createPoint(new Coordinate(geoLocation.getLatitudeE6(),
                geoLocation.getLongitudeE6())));
        this.id = id;
        this.name = name;
        this.description = description;
        this.serviceUrl = serviceUrl;
    }

    public FOI(String id, String name, String description, Point geometry,
            String serviceUrl) {
        super(geometry);
        this.id = id;
        this.name = name;
        this.description = description;
        this.serviceUrl = serviceUrl;
    }

}
