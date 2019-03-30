
package com.homeaway.placesearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenuePage implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
    }

    public VenuePage() {
    }

    protected VenuePage(Parcel in) {
        this.id = in.readString();
    }

    public static final Parcelable.Creator<VenuePage> CREATOR = new Parcelable.Creator<VenuePage>() {
        @Override
        public VenuePage createFromParcel(Parcel source) {
            return new VenuePage(source);
        }

        @Override
        public VenuePage[] newArray(int size) {
            return new VenuePage[size];
        }
    };
}
