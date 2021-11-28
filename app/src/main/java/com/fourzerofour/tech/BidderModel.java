package com.fourzerofour.tech;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BidderModel {

        private String BidItemUID = "NO";
        private String BidBy = "NO";
        private long BidUserPoints = 0;
        private long BidAmount = 0;
        private @ServerTimestamp Date BidDate;

    public BidderModel() {
    }

    public BidderModel(String bidItemUID, String bidBy, long bidUserPoints, long bidAmount, Date bidDate) {
        BidItemUID = bidItemUID;
        BidBy = bidBy;
        BidUserPoints = bidUserPoints;
        BidAmount = bidAmount;
        BidDate = bidDate;
    }

    public String getBidItemUID() {
        return BidItemUID;
    }

    public String getBidBy() {
        return BidBy;
    }

    public long getBidUserPoints() {
        return BidUserPoints;
    }

    public long getBidAmount() {
        return BidAmount;
    }

    public Date getBidDate() {
        return BidDate;
    }

    public void setBidItemUID(String bidItemUID) {
        BidItemUID = bidItemUID;
    }

    public void setBidBy(String bidBy) {
        BidBy = bidBy;
    }

    public void setBidUserPoints(long bidUserPoints) {
        BidUserPoints = bidUserPoints;
    }

    public void setBidAmount(long bidAmount) {
        BidAmount = bidAmount;
    }

    public void setBidDate(Date bidDate) {
        BidDate = bidDate;
    }
}
