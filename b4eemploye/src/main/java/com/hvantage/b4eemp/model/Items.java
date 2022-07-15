package com.hvantage.b4eemp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Items implements Parcelable {

    @SerializedName("order_unique_id")
    @Expose
    private String orderUniqueId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("product_title")
    @Expose
    private String productTitle;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("ltr_type")
    @Expose
    private String ltrType;
    @SerializedName("ltr_price")
    @Expose
    private String ltrPrice;
    @SerializedName("offer_id")
    @Expose
    private String offerId;
    @SerializedName("time_period")
    @Expose
    private String timePeriod;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("added_on")
    @Expose
    private String addedOn;

    int quntity = 1;

    int drawableImage;
    int isAddedtoCart;
    private boolean selected;

    public Items(){}

    protected Items(Parcel in) {
        orderUniqueId = in.readString();
        image = in.readString();
        productTitle = in.readString();
        productId = in.readString();
        ltrType = in.readString();
        ltrPrice = in.readString();
        offerId = in.readString();
        timePeriod = in.readString();
        price = in.readString();
        addedOn = in.readString();
        quntity = in.readInt();
        drawableImage = in.readInt();
        isAddedtoCart = in.readInt();
        selected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderUniqueId);
        dest.writeString(image);
        dest.writeString(productTitle);
        dest.writeString(productId);
        dest.writeString(ltrType);
        dest.writeString(ltrPrice);
        dest.writeString(offerId);
        dest.writeString(timePeriod);
        dest.writeString(price);
        dest.writeString(addedOn);
        dest.writeInt(quntity);
        dest.writeInt(drawableImage);
        dest.writeInt(isAddedtoCart);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Items> CREATOR = new Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel in) {
            return new Items(in);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };

    public String getOrderUniqueId() {
        return orderUniqueId;
    }

    public void setOrderUniqueId(String orderUniqueId) {
        this.orderUniqueId = orderUniqueId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLtrType() {
        return ltrType;
    }

    public void setLtrType(String ltrType) {
        this.ltrType = ltrType;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getDrawableImage() {
        return drawableImage;
    }

    public void setDrawableImage(int drawableImage) {
        this.drawableImage = drawableImage;
    }

    public int getQuntity() {
        return quntity;
    }

    public void setQuntity(int quntity) {
        this.quntity = quntity;
    }

    public boolean isAddedtoCart() {
        return isAddedtoCart != 0;
    }

    public void setAddedtoCart(int addedtoCart) {
        isAddedtoCart = addedtoCart;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public String getLtrPrice() {
        return ltrPrice;
    }

    public void setLtrPrice(String ltrPrice) {
        this.ltrPrice = ltrPrice;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
