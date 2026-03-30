package hcmute.edu.vn.pharmagnosis.repositories;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.models.Pharmacy;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class PharmacyRepository {

    private static class OverpassResponse {
        @SerializedName("elements")
        List<Element> elements;
    }

    private static class Element {
        long id;
        double lat;
        double lon;
        Tags tags;
    }

    private static class Tags {
        String name;
    }

    private interface OverpassApi {
        @GET("api/interpreter")
        Call<OverpassResponse> getPharmacies(@Query("data") String query);
    }

    private final OverpassApi api;

    public PharmacyRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://overpass-api.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(OverpassApi.class);
    }

    public LiveData<List<Pharmacy>> getPharmacies(double centerLat, double centerLon, int radius) {
        MutableLiveData<List<Pharmacy>> pharmacyLiveData = new MutableLiveData<>();

        String query = "[out:json];" +
                "(" +
                "nwr[\"amenity\"=\"pharmacy\"](around:" + radius + "," + centerLat + "," + centerLon + ");" +
                "nwr[\"healthcare\"=\"pharmacy\"](around:" + radius + "," + centerLat + "," + centerLon + ");" +
                ");" +
                "out center;";

        api.getPharmacies(query).enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Call<OverpassResponse> call, Response<OverpassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pharmacy> list = new ArrayList<>();
                    for (Element el : response.body().elements) {
                        Pharmacy p = new Pharmacy();
                        p.setPharmacyId(String.valueOf(el.id));
                        p.setName(el.tags != null && el.tags.name != null ? el.tags.name : "Nhà thuốc (Không tên)");
                        p.setLatitude(el.lat);
                        p.setLongitude(el.lon);
                        list.add(p);
                    }
                    pharmacyLiveData.setValue(list);
                }
            }

            @Override
            public void onFailure(Call<OverpassResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
                pharmacyLiveData.setValue(new ArrayList<>());
            }
        });

        return pharmacyLiveData;
    }
    public void saveSearchRecord(SearchRecord record) {
        // Trỏ tới bảng "SearchRecords" trên Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SearchRecords");

        // Tạo một ID ngẫu nhiên không trùng lặp cho record này
        String key = databaseReference.push().getKey();
        if (key != null) {
            record.setSearchId(key); // Gán ID vừa tạo vào object

            // Đẩy dữ liệu lên Firebase
            databaseReference.child(key).setValue(record)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Lưu lịch sử chỉ đường thành công!"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Lỗi lưu lịch sử: ", e));
        }
    }
}