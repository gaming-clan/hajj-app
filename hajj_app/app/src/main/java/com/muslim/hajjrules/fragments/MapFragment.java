package com.muslim.hajjrules.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.muslim.hajjrules.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapFragment extends Fragment {

    private MapView map;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load/initialize the osmdroid configuration
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        
        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        
        // Enable multitouch controls
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);
        
        // Set initial zoom and center on Mecca
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint meccaPoint = new GeoPoint(21.4225, 39.8262); // Mecca coordinates
        mapController.setCenter(meccaPoint);
        
        // Add important Hajj locations
        addHajjLocations();
        view.findViewById(R.id.button_center_mecca).setOnClickListener(v ->
                map.getController().animateTo(new GeoPoint(21.4225, 39.8262)));
        
        return view;
    }
    
    private void addHajjLocations() {
        // Kaaba/Masjid al-Haram
        Marker kaabaMarker = new Marker(map);
        kaabaMarker.setPosition(new GeoPoint(21.4225, 39.8262));
        kaabaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        kaabaMarker.setTitle("Qabja - Mesxhidul Haram");
        kaabaMarker.setSubDescription("Vendi më i shenjtë në Islam");
        map.getOverlays().add(kaabaMarker);
        
        // Mount Arafat
        Marker arafatMarker = new Marker(map);
        arafatMarker.setPosition(new GeoPoint(21.3544, 39.9857));
        arafatMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        arafatMarker.setTitle("Arafati");
        arafatMarker.setSubDescription("Vendi i qëndrimit në ditën e Arafatit");
        map.getOverlays().add(arafatMarker);
        
        // Muzdalifah
        Marker muzdalifaMarker = new Marker(map);
        muzdalifaMarker.setPosition(new GeoPoint(21.4067, 39.9364));
        muzdalifaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        muzdalifaMarker.setTitle("Muzdelife");
        muzdalifaMarker.setSubDescription("Lugina ku pelegrinët kalojnë natën pas Arafatit");
        map.getOverlays().add(muzdalifaMarker);
        
        // Mina
        Marker minaMarker = new Marker(map);
        minaMarker.setPosition(new GeoPoint(21.4120, 39.8884));
        minaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        minaMarker.setTitle("Mina");
        minaMarker.setSubDescription("Qyteti i tendave dhe vendi i Xhemerateve");
        map.getOverlays().add(minaMarker);
        
        // Masjid an-Nabawi (Medina)
        Marker medinaMarker = new Marker(map);
        medinaMarker.setPosition(new GeoPoint(24.4672, 39.6142));
        medinaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        medinaMarker.setTitle("Mesxhidun Nebevi");
        medinaMarker.setSubDescription("Xhamia e Profetit ﷺ në Medine");
        map.getOverlays().add(medinaMarker);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) getActivity().setTitle(R.string.map);
        // This will refresh the osmdroid configuration on resuming.
        map.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // This will refresh the osmdroid configuration on pausing.
        map.onPause();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            // Handle permission results if needed
        }
    }
}
