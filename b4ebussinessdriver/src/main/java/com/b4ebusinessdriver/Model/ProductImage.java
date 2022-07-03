package com.b4ebusinessdriver.Model;

/**
 * Created by manishsingh on 28/03/18.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductImage implements Parcelable
{

    @SerializedName("package_id")
    @Expose
    private String packageId;
    @SerializedName("delivery_id")
    @Expose
    private String deliveryId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("dimension")
    @Expose
    private String dimension;
    @SerializedName("remark")
    @Expose
    private String remark;
    public final static Parcelable.Creator<ProductImage> CREATOR = new Creator<ProductImage>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ProductImage createFromParcel(Parcel in) {
            return new ProductImage(in);
        }

        public ProductImage[] newArray(int size) {
            return (new ProductImage[size]);
        }

    }
            ;

    protected ProductImage(Parcel in) {
        this.packageId = ((String) in.readValue((String.class.getClassLoader())));
        this.deliveryId = ((String) in.readValue((String.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
        this.dimension = ((String) in.readValue((String.class.getClassLoader())));
        this.remark = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ProductImage() {
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(packageId);
        dest.writeValue(deliveryId);
        dest.writeValue(image);
        dest.writeValue(dimension);
        dest.writeValue(remark);
    }

    public int describeContents() {
        return 0;
    }

}