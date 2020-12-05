package com.hhb.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class AECU implements Parcelable {
    /**
     * 零件的PN
     * 例：7913600X4400
     */
    public String partNumber;


    /**
     * 零件的name
     * 例：ip
     */
    public String name;


    @Override
    public String toString() {
        return "AECU{" +
                "partNumber='" + partNumber + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.partNumber);
        dest.writeString(this.name);
    }

    public AECU() {
    }

    protected AECU(Parcel in) {
        this.partNumber = in.readString();
        this.name = in.readString();
    }

    public static final Creator<AECU> CREATOR = new Creator<AECU>() {
        @Override
        public com.hhb.aidl.AECU createFromParcel(Parcel source) {
            return new com.hhb.aidl.AECU(source);
        }

        @Override
        public com.hhb.aidl.AECU[] newArray(int size) {
            return new com.hhb.aidl.AECU[size];
        }
    };
}
