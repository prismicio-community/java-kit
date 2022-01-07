# Templating the GeoPoint Field

The GeoPoint field is used for Geolocation coordinates. It works by adding coordinates or by pasting a Google Maps URL.

## Get the latitude and longitude values

Here's an example of how to retrieve the latitude the longitude values from the GeoPoint field using the `getLatitude()` and `getLongitude()` methods.

```
<c:set var="location" value="${document.getGeoPoint('location.geopoint')}"/>
<p>Our coordinates: ${location.getLatitude()}, ${location.getLongitude()}</p>
```
