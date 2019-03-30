
package com.homeaway.placesearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LabeledLatLng implements Parcelable {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("lng")
    @Expose
    private float lng;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.label);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lng);
    }

    public LabeledLatLng() {
    }

    protected LabeledLatLng(Parcel in) {
        this.label = in.readString();
        this.lat = in.readFloat();
        this.lng = in.readFloat();
    }

    public static final Parcelable.Creator<LabeledLatLng> CREATOR = new Parcelable.Creator<LabeledLatLng>() {
        @Override
        public LabeledLatLng createFromParcel(Parcel source) {
            return new LabeledLatLng(source);
        }

        @Override
        public LabeledLatLng[] newArray(int size) {
            return new LabeledLatLng[size];
        }
    };
}
