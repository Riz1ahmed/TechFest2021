package com.fourzerofour.tech;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class VerifyModel {

    private @ServerTimestamp Date DDate;
    private String UIDverify = "NO";
    private String Valid = "NO";
    private String UID = "NO";
    private String NidOne = "NO";
    private String NidTwo = "NO";

    public VerifyModel() {
    }

    public VerifyModel(Date DDate, String UIDverify, String valid, String UID, String nidOne, String nidTwo) {
        this.DDate = DDate;
        this.UIDverify = UIDverify;
        Valid = valid;
        this.UID = UID;
        NidOne = nidOne;
        NidTwo = nidTwo;
    }

    public Date getDDate() {
        return DDate;
    }

    public String getUIDverify() {
        return UIDverify;
    }

    public String getValid() {
        return Valid;
    }

    public String getUID() {
        return UID;
    }

    public String getNidOne() {
        return NidOne;
    }

    public String getNidTwo() {
        return NidTwo;
    }
}
