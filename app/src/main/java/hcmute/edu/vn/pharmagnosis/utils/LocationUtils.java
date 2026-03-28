package hcmute.edu.vn.pharmagnosis.utils;

import android.content.Context;
import android.location.Geocoder;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    public static String getAddressFromLatLng(Context context, double lat, double lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Chưa xác định được địa chỉ cụ thể";
    }
}