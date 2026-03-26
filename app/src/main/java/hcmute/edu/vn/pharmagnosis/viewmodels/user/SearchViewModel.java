package hcmute.edu.vn.pharmagnosis.viewmodels.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import hcmute.edu.vn.pharmagnosis.models.Medicine;
import hcmute.edu.vn.pharmagnosis.repositories.MedicineRepository;

public class SearchViewModel extends ViewModel {

    private MedicineRepository medicineRepository;
    private LiveData<List<Medicine>> medicinesLiveData;

    public SearchViewModel() {
        medicineRepository = new MedicineRepository();
        // Kéo toàn bộ danh sách thuốc về ngay khi mở màn hình
        medicinesLiveData = medicineRepository.getAllMedicines();
    }

    // Nơi Fragment sẽ gọi để lấy danh sách thuốc
    public LiveData<List<Medicine>> getMedicinesLiveData() {
        return medicinesLiveData;
    }
}