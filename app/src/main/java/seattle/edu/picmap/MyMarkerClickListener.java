package seattle.edu.picmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

class MyMarkerClickListener implements GoogleMap.OnMarkerClickListener {

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }
}
