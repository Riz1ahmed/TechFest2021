package com.fourzerofour.tech;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    private @ServerTimestamp Date dExpairDate ;
    private @ServerTimestamp  Date dUploadDate ;
    private String ProductUID = "NO";
    private String UidOwner = "NO";
    private String UidProduct = "NO";
    private String UidCategory = "NO";
    private String UidBuyerFinal = "NO";
    private List<String> PhotoArrayUrl = new ArrayList<String>();
    private String Name = "NO";
    private String About = "NO";
    private String TagWords = "NO";
    private String BidMode = "NO";
    private String ExtraA = "NO";
    private String ExtraB = "NO";

    private long  iLowestBidPrice = 0;
    private long  iHighestBidPrice = 0;
    private long  iTotalBider = 0;
    private long  iExtra = 0;

    public ProductModel() {
    }

    public ProductModel(Date dExpairDate, Date dUploadDate, String productUID, String uidOwner, String uidProduct,
                        String uidCategory, String uidBuyerFinal, List<String> photoArrayUrl, String name, String about, String tagWords, String bidMode,
                        String extraA, String extraB, long iLowestBidPrice, long iHighestBidPrice, long iTotalBider, long iExtra) {
        this.dExpairDate = dExpairDate;
        this.dUploadDate = dUploadDate;
        ProductUID = productUID;
        UidOwner = uidOwner;
        UidProduct = uidProduct;
        UidCategory = uidCategory;
        UidBuyerFinal = uidBuyerFinal;
        PhotoArrayUrl = photoArrayUrl;
        Name = name;
        About = about;
        TagWords = tagWords;
        BidMode = bidMode;
        ExtraA = extraA;
        ExtraB = extraB;
        this.iLowestBidPrice = iLowestBidPrice;
        this.iHighestBidPrice = iHighestBidPrice;
        this.iTotalBider = iTotalBider;
        this.iExtra = iExtra;
    }

    public Date getdExpairDate() {
        return dExpairDate;
    }

    public Date getdUploadDate() {
        return dUploadDate;
    }

    public String getProductUID() {
        return ProductUID;
    }

    public String getUidOwner() {
        return UidOwner;
    }

    public String getUidProduct() {
        return UidProduct;
    }

    public String getUidCategory() {
        return UidCategory;
    }

    public String getUidBuyerFinal() {
        return UidBuyerFinal;
    }

    public List<String> getPhotoArrayUrl() {
        return PhotoArrayUrl;
    }

    public String getName() {
        return Name;
    }

    public String getAbout() {
        return About;
    }

    public String getTagWords() {
        return TagWords;
    }

    public String getBidMode() {
        return BidMode;
    }

    public String getExtraA() {
        return ExtraA;
    }

    public String getExtraB() {
        return ExtraB;
    }

    public long getiLowestBidPrice() {
        return iLowestBidPrice;
    }

    public long getiHighestBidPrice() {
        return iHighestBidPrice;
    }

    public long getiTotalBider() {
        return iTotalBider;
    }

    public long getiExtra() {
        return iExtra;
    }
}
