package hcmute.edu.vn.pharmagnosis.viewmodels.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import hcmute.edu.vn.pharmagnosis.models.Pharmacy;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;
import hcmute.edu.vn.pharmagnosis.repositories.PharmacyRepository;

public class PharmacyMapViewModel extends ViewModel {
    private final PharmacyRepository repository;

    public PharmacyMapViewModel() {
        repository = new PharmacyRepository();
    }

    public LiveData<List<Pharmacy>> fetchPharmacies(double lat, double lon, int radius) {
        return repository.getPharmacies(lat, lon, radius);
    }
    public void saveSearchRecord(SearchRecord record) {
        repository.saveSearchRecord(record);
    }
}