package seattle.edu.picmap;

public class PicMapLocation {
    private long mId;
    private final double mLatitude;
    private final double mLongitude;
    private final String mfileLocation;


    public PicMapLocation(int id, double latitude, double longitude, String fileLocation) {
        mId = id;
        mLatitude = latitude;
        mLongitude = longitude;
        mfileLocation = fileLocation;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getFileLocation() {
        return mfileLocation;
    }

    public void setId(long id) {
        mId = id;
    }

}
