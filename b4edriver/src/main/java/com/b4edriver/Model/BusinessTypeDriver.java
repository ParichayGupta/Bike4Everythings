package com.b4edriver.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MAX on 09-Jul-16.
 */
public class BusinessTypeDriver implements Parcelable {
    String id;
    String name;
    String key;
    int image;
    private boolean selected;

    public BusinessTypeDriver(){}

    protected BusinessTypeDriver(Parcel in) {
        id = in.readString();
        name = in.readString();
        key = in.readString();
        image = in.readInt();
        selected = in.readByte() != 0;
    }

    public static final Creator<BusinessTypeDriver> CREATOR = new Creator<BusinessTypeDriver>() {
        @Override
        public BusinessTypeDriver createFromParcel(Parcel in) {
            return new BusinessTypeDriver(in);
        }

        @Override
        public BusinessTypeDriver[] newArray(int size) {
            return new BusinessTypeDriver[size];
        }
    };

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(key);
        dest.writeInt(image);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
