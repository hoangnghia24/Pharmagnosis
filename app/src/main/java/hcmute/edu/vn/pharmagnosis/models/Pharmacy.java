package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
@IgnoreExtraProperties
public class Pharmacy {
    private String pharmacyId;
    private String name;
    private List<Address> addresses;

    public Pharmacy() {
    }

    public Pharmacy(String pharmacyId, String name, List<Address> addresses) {
        this.pharmacyId = pharmacyId;
        this.name = name;
        this.addresses = addresses;
    }

    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
