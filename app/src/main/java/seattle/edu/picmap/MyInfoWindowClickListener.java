package seattle.edu.picmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.File;

class MyInfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener{

    private final Context mContext;

    public MyInfoWindowClickListener(Context context) {
        this.mContext = context;
    }
    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(marker.getTitle());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), "image/jpeg");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
