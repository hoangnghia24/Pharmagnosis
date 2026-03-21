package hcmute.edu.vn.pharmagnosis.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

import hcmute.edu.vn.pharmagnosis.ENUM.ESearchType;
@IgnoreExtraProperties
public class SearchRecord {
    private String searchId;
    private String userId;
    private String keyword;
    private Date searchDate;
    private String itemId;
    private ESearchType searchType;

    public SearchRecord() {
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public ESearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(ESearchType searchType) {
        this.searchType = searchType;
    }

    public SearchRecord(String searchId, String userId, String keyword, Date searchDate, String itemId, ESearchType searchType) {
        this.searchId = searchId;
        this.userId = userId;
        this.keyword = keyword;
        this.searchDate = searchDate;
        this.itemId = itemId;
        this.searchType = searchType;
    }
}
