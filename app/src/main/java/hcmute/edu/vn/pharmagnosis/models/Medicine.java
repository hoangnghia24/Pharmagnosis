package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
@IgnoreExtraProperties
public class Medicine implements java.io.Serializable {
    private String medicineId;
    private String medicineName;
    private String tradeName;
    private List<String> activeIngredient;
    private String indications;
    private List<String> contraindications;
    private List<String> sideEffects;

    private String image;

    public Medicine() {
    }

    public Medicine(String medicineId, String medicineName, String tradeName, List<String> activeIngredient, String indications, List<String> contraindications, List<String> sideEffects, String image, String manufacturer) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.tradeName = tradeName;
        this.activeIngredient = activeIngredient;
        this.indications = indications;
        this.contraindications = contraindications;
        this.sideEffects = sideEffects;
        this.image = image;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public List<String> getActiveIngredient() {
        return activeIngredient;
    }

    public void setActiveIngredient(List<String> activeIngredient) {
        this.activeIngredient = activeIngredient;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public List<String> getContraindications() {
        return contraindications;
    }

    public void setContraindications(List<String> contraindications) {
        this.contraindications = contraindications;
    }

    public List<String> getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(List<String> sideEffects) {
        this.sideEffects = sideEffects;
    }
}
