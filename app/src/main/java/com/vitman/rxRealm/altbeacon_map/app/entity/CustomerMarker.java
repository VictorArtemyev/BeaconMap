package com.vitman.rxRealm.altbeacon_map.app.entity;

import android.widget.ImageView;

/**
 * Created by Victor Artemjev on 18.06.2015.
 */
public class CustomerMarker {

    private String mCustomerId;
    private Integer mBeaconId;
    private ImageView mMarker;

    public CustomerMarker(String customerId,
                          Integer beaconId,
                          ImageView marker) {
        mCustomerId = customerId;
        mBeaconId = beaconId;
        mMarker = marker;
    }

    public String getCustomerId() {
        return mCustomerId;
    }

    public Integer getBeaconId() {
        return mBeaconId;
    }

    public ImageView getMarker() {
        return mMarker;
    }

    @Override
    public String toString() {
        return "CustomerMarker{" +
                "mCustomerId='" + mCustomerId + '\'' +
                ", mBeaconId=" + mBeaconId +
                ", mMarker=" + mMarker.getTag() +
                '}';
    }
}
