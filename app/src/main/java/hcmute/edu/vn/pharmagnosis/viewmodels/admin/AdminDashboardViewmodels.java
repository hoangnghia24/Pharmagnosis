package hcmute.edu.vn.pharmagnosis.viewmodels.admin;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.pharmagnosis.ENUM.ESearchType;
import hcmute.edu.vn.pharmagnosis.models.SearchRecord;
import hcmute.edu.vn.pharmagnosis.repositories.SearchRepository;

public class AdminDashboardViewmodels extends ViewModel {

    public static class TopSearchData {
        public ArrayList<BarEntry> entries = new ArrayList<>();
        public ArrayList<String> labels = new ArrayList<>();
        public ESearchType currentType;
    }

    private final MutableLiveData<TopSearchData> chartDataLiveData = new MutableLiveData<>();
    private final SearchRepository repository;
    private List<SearchRecord> allRecords = new ArrayList<>();

    public AdminDashboardViewmodels() {
        repository = new SearchRepository();
    }

    public LiveData<TopSearchData> getChartDataLiveData() {
        return chartDataLiveData;
    }

    public void fetchTopSearchData(int monthSelected, ESearchType typeSelected) {
        repository.getSearchRecords(new SearchRepository.OnSearchDataFetched() {
            @Override
            public void onSuccess(List<SearchRecord> records) {
                allRecords = records;
                processTop10Data(monthSelected, typeSelected);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MVVM_Debug", "Lỗi Firebase: " + errorMessage);
            }
        });
    }

    public void reprocessData(int monthSelected, ESearchType typeSelected) {
        if (!allRecords.isEmpty()) {
            processTop10Data(monthSelected, typeSelected);
        } else {
            // ĐÃ THÊM: Nếu dữ liệu chưa có, ép nó gọi mạng lại!
            fetchTopSearchData(monthSelected, typeSelected);
        }
    }

    private void processTop10Data(int monthSelected, ESearchType typeSelected) {
        Map<String, Integer> keywordCountMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();

        // LOG KIỂM TRA DỮ LIỆU CÓ CHÍNH XÁC KHÔNG
        int countMed = 0;
        int countPhar = 0;

        for (SearchRecord record : allRecords) {
            // Đếm tổng số lượng 2 loại
            if (record.getSearchType() == ESearchType.MEDICINE) countMed++;
            if (record.getSearchType() == ESearchType.PHARMACY) countPhar++;

            if (record.getSearchDate() != null && record.getKeyword() != null) {
                if (record.getSearchType() == typeSelected) {
                    boolean isMatchMonth = false;
                    if (monthSelected == 0) {
                        isMatchMonth = true;
                    } else {
                        cal.setTime(record.getSearchDate());
                        if (cal.get(Calendar.MONTH) == (monthSelected - 1)) {
                            isMatchMonth = true;
                        }
                    }

                    if (isMatchMonth) {
                        String kw = record.getKeyword();
                        keywordCountMap.put(kw, keywordCountMap.getOrDefault(kw, 0) + 1);
                    }
                }
            }
        }

        // Bắn Logcat ra màn hình
        Log.d("MVVM_Debug", "Đang vẽ biểu đồ: " + typeSelected.name() +
                " | Tổng Thuốc trong DB: " + countMed +
                " | Tổng Nhà Thuốc trong DB: " + countPhar);

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(keywordCountMap.entrySet());
        Collections.sort(sortedList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        TopSearchData chartData = new TopSearchData();
        chartData.currentType = typeSelected;

        int limit = Math.min(10, sortedList.size());
        for (int i = 0; i < limit; i++) {
            chartData.labels.add(sortedList.get(i).getKey());
            chartData.entries.add(new BarEntry(i, sortedList.get(i).getValue()));
        }

        chartDataLiveData.postValue(chartData);
    }
}