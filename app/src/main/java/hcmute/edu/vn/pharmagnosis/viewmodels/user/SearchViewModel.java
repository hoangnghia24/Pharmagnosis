package hcmute.edu.vn.pharmagnosis.viewmodels.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.pharmagnosis.ENUM.ESearchType;
import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;
import hcmute.edu.vn.pharmagnosis.repositories.MedicineRepository;

public class SearchViewModel extends ViewModel {

    private MedicineRepository medicineRepository;
    private LiveData<List<Medicine>> medicinesLiveData;
    private MutableLiveData<List<SearchRecord>> historyLiveData = new MutableLiveData<>();
    private DatabaseReference historyRef;
    private String currentUserId;

    public SearchViewModel() {
        medicineRepository = new MedicineRepository();
        medicinesLiveData = medicineRepository.getAllMedicines();
        
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            historyRef = FirebaseDatabase.getInstance().getReference("SearchRecords");
            loadSearchHistory();
        }
    }

    public LiveData<List<Medicine>> getMedicinesLiveData() {
        return medicinesLiveData;
    }

    public LiveData<List<SearchRecord>> getHistoryLiveData() {
        return historyLiveData;
    }

    public void loadSearchHistory() {
        if (currentUserId == null) return;

        historyRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Sử dụng Map để lọc trùng lặp từ khóa, giữ lại bản ghi mới nhất cho mỗi từ khóa
                Map<String, SearchRecord> uniqueHistoryMap = new LinkedHashMap<>();
                
                List<SearchRecord> allRecords = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SearchRecord record = ds.getValue(SearchRecord.class);
                    if (record != null) {
                        allRecords.add(record);
                    }
                }

                Collections.sort(allRecords, (o1, o2) -> o1.getSearchDate().compareTo(o2.getSearchDate()));
                
                for (SearchRecord record : allRecords) {
                    uniqueHistoryMap.put(record.getKeyword().toLowerCase(), record);
                }
                
                List<SearchRecord> resultList = new ArrayList<>(uniqueHistoryMap.values());
                
                // Sắp xếp lại để cái mới nhất lên đầu
                Collections.sort(resultList, (o1, o2) -> o2.getSearchDate().compareTo(o1.getSearchDate()));
                
                // Giới hạn hiển thị 10 mục gần nhất
                if (resultList.size() > 10) {
                    resultList = resultList.subList(0, 10);
                }
                
                historyLiveData.setValue(resultList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void addSearchHistory(String keyword) {
        if (currentUserId == null || keyword == null || keyword.trim().isEmpty()) return;

        String cleanKeyword = keyword.trim();
        
        // Kiểm tra xem đã có từ khóa này chưa để cập nhật thời gian thay vì tạo mới
        historyRef.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String existingKey = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    SearchRecord record = ds.getValue(SearchRecord.class);
                    if (record != null && record.getKeyword().equalsIgnoreCase(cleanKeyword)) {
                        existingKey = ds.getKey();
                        break;
                    }
                }

                if (existingKey != null) {
                    // Nếu đã tồn tại, chỉ cập nhật ngày tìm kiếm mới nhất
                    historyRef.child(existingKey).child("searchDate").setValue(new Date());
                } else {
                    // Nếu chưa có, tạo bản ghi mới
                    String id = historyRef.push().getKey();
                    SearchRecord record = new SearchRecord(id, currentUserId, cleanKeyword, new Date(), null, ESearchType.MEDICINE);
                    if (id != null) {
                        historyRef.child(id).setValue(record);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void deleteHistoryRecord(SearchRecord record) {
        if (record.getSearchId() != null) {
            historyRef.child(record.getSearchId()).removeValue();
        }
    }
}
