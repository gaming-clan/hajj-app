package com.muslim.hajjrules.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapUtils {
    private static final double MECCA_LAT = 21.422487;
    private static final double MECCA_LONG = 39.826206;
    private static final int DEFAULT_ZOOM = 15;

    public static void initializeMap(Context context, MapView mapView) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(DEFAULT_ZOOM);
        mapView.getController().setCenter(new GeoPoint(MECCA_LAT, MECCA_LONG));
    }

    public static void addLocationOverlay(Context context, MapView mapView) {
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);
    }

    public static Marker addMarker(MapView mapView, double lat, double lon, String title, String snippet, Drawable icon) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        if (icon != null) {
            marker.setIcon(icon);
        }
        mapView.getOverlays().add(marker);
        return marker;
    }

    public static void centerOnLocation(MapView mapView, Location location, float zoom) {
        if (location != null) {
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapView.getController().animateTo(point);
            if (zoom > 0) {
                mapView.getController().setZoom(zoom);
            }
        }
    }

    public static void enableMapCache(Context context) {
        Configuration.getInstance().setCacheMapTileCount((short)12);
        Configuration.getInstance().setCacheMapTileOvershoot((short)12);
        Configuration.getInstance().setTileDownloadThreads((short)4);
    }

    public static void clearMapCache(Context context) {
        new SqlTileWriter().purgeCache();
    }
}
