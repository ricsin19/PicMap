package seattle.edu.picmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.File;

class MyInfoViewAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater mInflater;

    public MyInfoViewAdapter(LayoutInflater inflater) {
        this.mInflater=inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        @SuppressLint("InflateParams")
        View v = mInflater.inflate(R.layout.marker_img_layout,null);
        File imgFile = new File(marker.getTitle());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView imgView = (ImageView) v.findViewById(R.id.imageView);
            imgView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 150, 150, false));
        }
        return v;
    }
}
