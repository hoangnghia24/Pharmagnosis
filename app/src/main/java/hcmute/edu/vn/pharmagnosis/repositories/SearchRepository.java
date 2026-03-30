package hcmute.edu.vn.pharmagnosis.repositories;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.pharmagnosis.ENUM.ESearchType;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;

public class SearchRepository {
    private final DatabaseReference databaseReference;

    public SearchRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference("SearchRecords");
    }

    public interface OnSearchDataFetched {
        void onSuccess(List<SearchRecord> records);
        void onError(String errorMessage);
    }

    public void getSearchRecords(OnSearchDataFetched callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SearchRecord> records = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        SearchRecord record = new SearchRecord();

                        // 1. Bóc tách dữ liệu cơ bản
                        Object searchIdObj = postSnapshot.child("searchId").getValue();
                        if (searchIdObj != null) record.setSearchId(String.valueOf(searchIdObj));

                        Object userIdObj = postSnapshot.child("userId").getValue();
                        if (userIdObj != null) record.setUserId(String.valueOf(userIdObj));

                        Object keywordObj = postSnapshot.child("keyword").getValue();
                        if (keywordObj != null) record.setKeyword(String.valueOf(keywordObj));

                        Object itemIdObj = postSnapshot.child("itemId").getValue();
                        if (itemIdObj != null) record.setItemId(String.valueOf(itemIdObj));

                        // 2. Bóc tách Enum
                        Object typeObj = postSnapshot.child("searchType").getValue();
                        if (typeObj != null) {
                            String typeStr = String.valueOf(typeObj).toUpperCase().trim();
                            if (typeStr.equals("PHARMACY")) {
                                record.setSearchType(ESearchType.PHARMACY);
                            } else if (typeStr.equals("MEDICINE") || typeStr.equals("DRUG")) {
                                record.setSearchType(ESearchType.MEDICINE);
                            }
                        }

                        // 3. CỨU CÁNH: Bóc tách Date chống lỗi 100%
                        DataSnapshot dateSnapshot = postSnapshot.child("searchDate");
                        if (dateSnapshot.exists()) {
                            try {
                                if (dateSnapshot.hasChild("time")) {
                                    Long time = dateSnapshot.child("time").getValue(Long.class);
                                    if (time != null) record.setSearchDate(new java.util.Date(time));
                                } else {
                                    Object rawDate = dateSnapshot.getValue();
                                    if (rawDate instanceof Long) {
                                        record.setSearchDate(new java.util.Date((Long) rawDate));
                                    } else {
                                        // Gán tạm thời gian hiện tại nếu format lạ để cứu từ khóa
                                        record.setSearchDate(new java.util.Date());
                                        android.util.Log.e("MVVM_Debug", "Format Date lạ ở ID: " + postSnapshot.getKey() + " -> Đã ép dùng giờ hiện tại");
                                    }
                                }
                            } catch (Exception e) {
                                record.setSearchDate(new java.util.Date());
                                android.util.Log.e("MVVM_Debug", "Lỗi ép Date ở ID: " + postSnapshot.getKey() + " -> Đã ép dùng giờ hiện tại");
                            }
                        } else {
                            // Nếu mất hoàn toàn trường Date, vẫn gán giờ hiện tại để không bị vứt bỏ
                            record.setSearchDate(new java.util.Date());
                        }

                        // 4. Kiểm duyệt và thông báo chính xác lỗi
                        if (record.getKeyword() == null) {
                            android.util.Log.e("MVVM_Debug", "BỊ LOẠI: ID " + postSnapshot.getKey() + " bị rỗng Từ khóa (keyword)");
                        } else if (record.getSearchType() == null) {
                            android.util.Log.e("MVVM_Debug", "BỊ LOẠI: ID " + postSnapshot.getKey() + " bị rỗng Loại (searchType)");
                        } else {
                            records.add(record);
                        }

                    } catch (Exception e) {
                        android.util.Log.e("MVVM_Debug", "Lỗi Exception TỔNG ở ID " + postSnapshot.getKey() + ": " + e.getMessage());
                    }
                }

                android.util.Log.d("MVVM_Debug", "Repository đã xuất đi " + records.size() + " bản ghi hợp lệ.");
                callback.onSuccess(records);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}