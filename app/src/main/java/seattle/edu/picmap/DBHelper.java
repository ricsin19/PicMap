package seattle.edu.picmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PICMAP.db";
    private static final int DATABASE_VERSION = 1;



    private static final class PicMapColumns implements BaseColumns {
        private PicMapColumns() {}
        public static final String TABLE_NAME = "picmap";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String FILELOCATION = "filelocation";
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates a database with a single table.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PicMapColumns.TABLE_NAME + " ("
                + PicMapColumns._ID + " integer primary key autoincrement, "
                + PicMapColumns.LATITUDE + " real, "
                + PicMapColumns.LONGITUDE + " real, "
                + PicMapColumns.FILELOCATION + " text"
                + ");");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not used but required to implement (SQLiteOpenHelper)
    }

        private ContentValues getLocation(PicMapLocation picmap) {
        ContentValues cv = new ContentValues();
        cv.put(PicMapColumns.LATITUDE, picmap.getLatitude());
        cv.put(PicMapColumns.LONGITUDE, picmap.getLongitude());
        cv.put(PicMapColumns.FILELOCATION, picmap.getFileLocation());
        return cv;
    }

    // Inserts a blank new location into the database, returns the location with
    // the id number set.
    public PicMapLocation insertLocation(double latitude, double longitude, String fileLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        PicMapLocation picmap = new PicMapLocation(-1, latitude, longitude, fileLocation);
        ContentValues row = getLocation(picmap);
        long id = db.insert(PicMapColumns.TABLE_NAME,null,row);
        picmap.setId(id);
        return picmap;
    }


    // Returns a cursor that has a list for all exiting locations (marker info).
    public LocationCursor queryLocations() {
        Cursor cursor = getReadableDatabase().query(
                PicMapColumns.TABLE_NAME,      // table name
                null,                   // columns (all)
                null,                   // where (all rows)
                null,                   // whereArgs
                null,                   // group by
                null,                   // having
                PicMapColumns._ID + " asc",  // order by
                null);                  // limit

        return (new LocationCursor(cursor));
    }
    
    public void deleteALL() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PicMapColumns.TABLE_NAME, null, null);
    }

    public static class LocationCursor extends CursorWrapper {

        public LocationCursor(Cursor cursor) {
            super(cursor);
        }

        public PicMapLocation getLocation() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            @SuppressWarnings("UnnecessaryLocalVariable") PicMapLocation location = new PicMapLocation(
                    getInt(getColumnIndex(PicMapColumns._ID)),
                    getDouble(getColumnIndex(PicMapColumns.LATITUDE)),
                    getDouble(getColumnIndex(PicMapColumns.LONGITUDE)),
                    getString(getColumnIndex(PicMapColumns.FILELOCATION)));
            return location;
        }
    }
}
