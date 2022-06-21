package com.bike4everythingbussiness.Model;

/**
 * Created by manishsingh on 15/02/18.
 */
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wallet implements Parcelable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("wallet")
    @Expose
    private String wallet;
    @SerializedName("sso_token")
    @Expose
    private String ssoToken;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    public final static Parcelable.Creator<Wallet> CREATOR = new Creator<Wallet>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        public Wallet[] newArray(int size) {
            return (new Wallet[size]);
        }

    }
            ;

    protected Wallet(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.wallet = ((String) in.readValue((String.class.getClassLoader())));
        this.ssoToken = ((String) in.readValue((String.class.getClassLoader())));
        this.mobileNo = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Wallet() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(wallet);
        dest.writeValue(ssoToken);
        dest.writeValue(mobileNo);
    }

    public int describeContents() {
        return 0;
    }

}