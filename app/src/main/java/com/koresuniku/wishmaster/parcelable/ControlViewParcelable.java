package com.koresuniku.wishmaster.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

public class ControlViewParcelable implements Parcelable {
    private String currentDurationString;
    private String overallDurationString;
    private long currentPositionMillis;

    public ControlViewParcelable(String s1, String s2, long l) {
        this.currentDurationString = s1;
        this.overallDurationString = s2;
        this.currentPositionMillis = l;
    }

    protected ControlViewParcelable(Parcel in) {
        currentDurationString = in.readString();
        overallDurationString = in.readString();
        currentPositionMillis = in.readLong();
    }

    public static final Creator<ControlViewParcelable> CREATOR = new Creator<ControlViewParcelable>() {
        @Override
        public ControlViewParcelable createFromParcel(Parcel in) {
            return new ControlViewParcelable(in);
        }

        @Override
        public ControlViewParcelable[] newArray(int size) {
            return new ControlViewParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentDurationString);
        dest.writeString(overallDurationString);
        dest.writeLong(currentPositionMillis);
    }
}
