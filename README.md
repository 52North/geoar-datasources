# GeoAR Datasources

This repository provides a collection of data sources for the geolocation-based augmented reality browser GeoAR.

## Documentation

https://wiki.52north.org/bin/view/Projects/GeoARDatasource

## Development

If you want to use these datasources, you have to build them manually and install them into your Maven local repository so that the dependencies can be resolved (git clone, then ``mvn clean install``). Then you can add a datasource as a dependencies in the geoar-app project.

## Datasources

* SIR/SOS Datasource: Discover sensors and view their data based on the Sensor Web services SIR and SOS > https://wiki.52north.org/bin/view/Projects/GeoARDatasourceSirSos
* Wikilocation Datasource: Find Wikipedia articles for attractions nearby > https://wiki.52north.org/bin/view/Projects/GeoARDatasourceWikilocation
* NoiseDroid Datasource: Find noise community based noise measurements > https://wiki.52north.org/bin/edit/Projects/GeoARDatasourceNoiseDroid
* Cosm Datasource: Discover sensors from the Cosm platform > https://wiki.52north.org/bin/view/Projects/GeoARDatasourceCosm

## License

GeoAR is published under Apache Software License, Version 2.0.

Used libraries are listed in the NOTICE files of each project.

## Support / Contact

If you have questions using or developing GeoAR and it's projects, please contact the Geostatistics Community mailing list or the synced forum at http://list.52north.org/mailman/listinfo/geostatistics respectively http://geostatistics.forum.52north.org/. If you want to report bugs or ask questions related to the code, please submit an issue at https://github.com/52North/geoar-app/issues.

Project manager: Daniel Nüst (d.nuest@52north.org) 
